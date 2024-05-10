/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.app

import com.fluxtah.ask.Version
import com.fluxtah.ask.api.FunctionInvoker
import com.fluxtah.ask.api.assistants.AssistantInstallRepository
import com.fluxtah.ask.api.assistants.AssistantRegistry
import com.fluxtah.ask.api.clients.openai.assistants.AssistantsApi
import com.fluxtah.ask.api.clients.openai.assistants.model.AssistantRun
import com.fluxtah.ask.api.clients.openai.assistants.model.AssistantRunStepDetails
import com.fluxtah.ask.api.clients.openai.assistants.model.RunRequest
import com.fluxtah.ask.api.clients.openai.assistants.model.RunStatus
import com.fluxtah.ask.api.clients.openai.assistants.model.SubmitToolOutputsRequest
import com.fluxtah.ask.api.clients.openai.assistants.model.ToolOutput
import com.fluxtah.ask.api.plugins.AskPluginLoader
import com.fluxtah.ask.api.pollRunStatus
import com.fluxtah.ask.api.store.PropertyStore
import com.fluxtah.ask.app.commanding.CommandFactory
import com.fluxtah.ask.assistants.coder.CoderAssistant
import com.fluxtah.askpluginsdk.AssistantDefinition
import com.fluxtah.askpluginsdk.logging.AskLogger
import com.fluxtah.askpluginsdk.logging.LogLevel
import kotlinx.coroutines.runBlocking
import java.io.File
import kotlin.system.exitProcess

class App(
    private val logger: AskLogger = AskLogger(),
    private val userProperties: UserProperties = UserProperties(PropertyStore("user.properties")),
    private val assistantsApi: AssistantsApi = AssistantsApi(
        apiKeyProvider = { userProperties.getOpenaiApiKey() }
    ),
    private val assistantRegistry: AssistantRegistry = AssistantRegistry(),
    private val assistantInstallRepository: AssistantInstallRepository = AssistantInstallRepository(assistantsApi),
    private val commandFactory: CommandFactory = CommandFactory(
        logger,
        assistantsApi,
        assistantRegistry,
        assistantInstallRepository,
        userProperties
    ),
    private val functionInvoker: FunctionInvoker = FunctionInvoker(),
) {
    init {
        userProperties.load()

        // Register built in assistants
        assistantRegistry.register(CoderAssistant(logger))
        // assistantRegistry.register(GitAssistant(logger))
        // assistantRegistry.register(ManPageAssistant(logger))

        // Load plugin assistants
        AskPluginLoader(logger).loadPlugins().forEach {
            assistantRegistry.register(it)
        }
    }

    fun runOneShotCommand(command: String) {
        handleInput(command)
        exitProcess(0)
    }

    fun run() {
        printWelcomeMessage()

        val logLevel = userProperties.getLogLevel()
        if (logLevel != LogLevel.OFF) {
            logger.log(LogLevel.DEBUG, "Log level: $logLevel")
        }

        logger.setLogLevel(logLevel)

        while (true) {
            print("$ ")

            try {
                val input = readln()

                handleInput(input)
            } catch (e: Exception) {
                logger.log(LogLevel.ERROR, "Error: ${e.message}")
            }
        }
    }

    fun debugPlugin(pluginFile: File) {
        assistantRegistry.register(AskPluginLoader(logger).loadPlugin(pluginFile))

        run()
    }

    private fun handleInput(input: String) {
        if (input.isEmpty()) {
            return
        }

        try {
            if (input.startsWith("/")) {
                val command = commandFactory.create(input)
                if (command.requiresApiKey) {
                    if (userProperties.getOpenaiApiKey().isEmpty()) {
                        println("You need to set an OpenAI API key first! with /set-key <api-key>")
                        return
                    }
                }
                runBlocking {
                    command.execute()
                }
            } else {
                runBlocking {
                    handlePrompt(input)
                }
            }
        } catch (e: Exception) {
            println("Error: ${e.stackTraceToString()}")
        }
    }

    private suspend fun handlePrompt(prompt: String) {
        val currentThreadId = userProperties.getThreadId()

        if (currentThreadId.isEmpty()) {
            println("You need to create a thread first. Use /thread-new")
            return
        }

        if (!prompt.startsWith("@") && userProperties.getAssistantId().isEmpty()) {
            println("You need to address an assistant with @assistant-id <prompt>, to see available assistants use /assistant-list")
            return
        }

        val assistantId = if (prompt.startsWith("@")) {
            val parts = prompt.split(" ")
            val assistantId = parts[0].substring(1)
            parts.drop(1).joinToString(" ")
            assistantId
        } else {
            userProperties.getAssistantId()
        }

        val assistantDef = assistantRegistry.getAssistantById(assistantId)

        if (assistantDef == null) {
            println("Assistant definition not found: $assistantId")
            return
        }

        val assistantInstallRecord = assistantInstallRepository.getAssistantInstallRecord(assistantDef.id)

        if (assistantInstallRecord == null) {
            println("Assistant not installed: $assistantId, to install use /assistant-install $assistantId")
            return
        }

        val userMessage = assistantsApi.messages.createUserMessage(currentThreadId, prompt)
        val createRun = assistantsApi.runs.createRun(
            currentThreadId, RunRequest(
                assistantId = assistantInstallRecord.installId,
                model = userProperties.getModel().ifEmpty {
                    null
                },
            )
        )
        userProperties.setRunId(createRun.id)
        userProperties.setAssistantId(assistantId)
        userProperties.save()

        processRun(assistantDef, createRun, currentThreadId)

        val lastMessage =
            assistantsApi.messages.listMessages(
                threadId = currentThreadId,
                beforeId = userMessage.id
            ).data.firstOrNull()
        if (lastMessage != null) {
            if (userMessage.id != lastMessage.id) {
                println(lastMessage.content.first().text.value)
            }
        }
    }

    private suspend fun processRun(
        assistantDef: AssistantDefinition,
        startRun: AssistantRun,
        currentThreadId: String
    ) {
        var currentRun = startRun
        print(" ")
        while (pollRunStatus(assistantsApi, currentThreadId, startRun) { status ->
                print(" > $status")
            }.status == RunStatus.REQUIRES_ACTION) {
            currentRun = executeRunSteps(assistantDef, currentThreadId, currentRun)
        }

        println(" > ${currentRun.status}")
        println()
    }

    private suspend fun executeRunSteps(
        assistantDef: AssistantDefinition,
        currentThreadId: String,
        run: AssistantRun
    ): AssistantRun {
        val steps = assistantsApi.runs.listRunSteps(currentThreadId, run.id)

        steps.data.forEach { step ->
            when (val details = step.stepDetails) {
                is AssistantRunStepDetails.MessageCreation -> {}
                is AssistantRunStepDetails.ToolCalls -> {
                    return executeTools(assistantDef, currentThreadId, run, details)
                }
            }
        }
        return run
    }

    private suspend fun executeTools(
        assistant: AssistantDefinition,
        currentThreadId: String,
        run: AssistantRun,
        details: AssistantRunStepDetails.ToolCalls
    ): AssistantRun {
        val toolOutputs = mutableListOf<ToolOutput>()

        details.toolCalls.forEach { toolCall ->
            when (toolCall) {
                is AssistantRunStepDetails.ToolCalls.ToolCallDetails.FunctionToolCallDetails -> {
                    logger.log(
                        LogLevel.DEBUG,
                        "[Exec Fun] ${toolCall.function.name}: ${toolCall.function.arguments.take(200)}..."
                    )
                    val result = functionInvoker.invokeFunction(assistant.functions, toolCall)
                    toolOutputs.add(
                        ToolOutput(
                            toolCall.id,
                            result
                        )
                    )
                    logger.log(LogLevel.DEBUG, "[Fun Result] $result")
                }
            }
        }

        return submitToolOutputs(toolOutputs, currentThreadId, run)
    }

    private suspend fun submitToolOutputs(
        toolOutputs: MutableList<ToolOutput>,
        currentThreadId: String,
        run: AssistantRun
    ) = if (toolOutputs.isNotEmpty()) {
        assistantsApi.runs.submitToolOutputs(
            currentThreadId,
            run.id,
            SubmitToolOutputsRequest(toolOutputs)
        )
    } else {
        run
    }

    private fun printWelcomeMessage() {
        println(
            """
         ░▒▓██████▓▒░ ░▒▓███████▓▒░▒▓█▓▒░░▒▓█▓▒░ 
        ░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░      ░▒▓█▓▒░░▒▓█▓▒░ 
        ░▒▓████████▓▒░░▒▓██████▓▒░░▒▓██████▓▒░  
        ░▒▓█▓▒░░▒▓█▓▒░      ░▒▓█▓▒░▒▓█▓▒░░▒▓█▓▒░ 
        ░▒▓█▓▒░░▒▓█▓▒░▒▓███████▓▒░░▒▓█▓▒░░▒▓█▓▒░             
        """.trimIndent()
        )
        println("░▒▓█▓░ ASSISTANT KOMMANDER v${Version.APP_VERSION} ░▓█▓▒░")
        println()
        println("Assistants available:")
        runBlocking {
            commandFactory.create("/assistant-list").execute()
        }
        println()
        println("Type /help for a list of commands")
        println()
    }
}
