/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.app

import com.fluxtah.ask.Version
import com.fluxtah.ask.api.AssistantRunner
import com.fluxtah.ask.api.tools.fn.FunctionInvoker
import com.fluxtah.ask.api.assistants.AssistantInstallRepository
import com.fluxtah.ask.api.assistants.AssistantRegistry
import com.fluxtah.ask.api.clients.openai.assistants.AssistantsApi
import com.fluxtah.ask.api.plugins.AskPluginLoader
import com.fluxtah.ask.api.printers.AskConsoleResponsePrinter
import com.fluxtah.ask.api.printers.AskResponsePrinter
import com.fluxtah.ask.api.store.PropertyStore
import com.fluxtah.ask.app.commanding.CommandFactory
import com.fluxtah.ask.assistants.coder.CoderAssistant
import com.fluxtah.askpluginsdk.logging.AskLogger
import com.fluxtah.askpluginsdk.logging.LogLevel
import kotlinx.coroutines.runBlocking
import java.io.File
import kotlin.system.exitProcess

class ConsoleApplication(
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
    private val responsePrinter: AskResponsePrinter = AskConsoleResponsePrinter(),
    private val assistantRunner: AssistantRunner = AssistantRunner(
        logger = logger,
        userProperties = userProperties,
        assistantsApi = assistantsApi,
        assistantRegistry = assistantRegistry,
        assistantInstallRepository = assistantInstallRepository,
        functionInvoker = FunctionInvoker(),
        responsePrinter = responsePrinter
    ),
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
                        responsePrinter.println("You need to set an OpenAI API key first! with /set-key <api-key>")
                        return
                    }
                }
                runBlocking {
                    command.execute()
                }
            } else {
                runBlocking {
                    val currentThreadId = userProperties.getThreadId()

                    if (currentThreadId.isEmpty()) {
                        responsePrinter.println("You need to create a thread first. Use /thread-new")
                    }

                    if (!input.startsWith("@") && userProperties.getAssistantId().isEmpty()) {
                        responsePrinter.println("You need to address an assistant with @assistant-id <prompt>, to see available assistants use /assistant-list")
                    } else {

                        val assistantId = if (input.startsWith("@")) {
                            val parts = input.split(" ")
                            val assistantId = parts[0].substring(1)
                            parts.drop(1).joinToString(" ")
                            assistantId
                        } else {
                            userProperties.getAssistantId()
                        }

                        assistantRunner.run(assistantId, currentThreadId, input)
                    }
                }
            }
        } catch (e: Exception) {
            responsePrinter.println("Error: ${e.message}, run with /log-level ERROR for more info")
            logger.log(LogLevel.ERROR, "Error: ${e.stackTraceToString()}")
        }
    }

    private fun printWelcomeMessage() {
        responsePrinter.println(
            """
         ░▒▓██████▓▒░ ░▒▓███████▓▒░▒▓█▓▒░░▒▓█▓▒░ 
        ░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░      ░▒▓█▓▒░░▒▓█▓▒░ 
        ░▒▓████████▓▒░░▒▓██████▓▒░░▒▓██████▓▒░  
        ░▒▓█▓▒░░▒▓█▓▒░      ░▒▓█▓▒░▒▓█▓▒░░▒▓█▓▒░ 
        ░▒▓█▓▒░░▒▓█▓▒░▒▓███████▓▒░░▒▓█▓▒░░▒▓█▓▒░             
        """.trimIndent()
        )
        responsePrinter.println("░▒▓█▓░ ASSISTANT KOMMANDER v${Version.APP_VERSION} ░▓█▓▒░")
        responsePrinter.println()
        responsePrinter.println("Assistants available:")
        runBlocking {
            commandFactory.create("/assistant-list").execute()
        }
        responsePrinter.println()
        responsePrinter.println("Type /help for a list of commands")
        responsePrinter.println()
    }
}

