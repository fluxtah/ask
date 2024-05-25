/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.app

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import com.fluxtah.ask.Version
import com.fluxtah.ask.api.AssistantRunner
import com.fluxtah.ask.api.RunDetails
import com.fluxtah.ask.api.RunResult
import com.fluxtah.ask.api.ansi.green
import com.fluxtah.ask.api.assistants.AssistantInstallRepository
import com.fluxtah.ask.api.assistants.AssistantRegistry
import com.fluxtah.ask.api.clients.openai.assistants.AssistantsApi
import com.fluxtah.ask.api.clients.openai.assistants.model.RunStatus
import com.fluxtah.ask.api.clients.openai.assistants.model.TruncationStrategy
import com.fluxtah.ask.api.plugins.AskPluginLoader
import com.fluxtah.ask.api.printers.AskConsoleResponsePrinter
import com.fluxtah.ask.api.printers.AskResponsePrinter
import com.fluxtah.ask.api.repository.ThreadRepository
import com.fluxtah.ask.api.store.PropertyStore
import com.fluxtah.ask.api.tools.fn.FunctionInvoker
import com.fluxtah.ask.app.commanding.CommandFactory
import com.fluxtah.ask.app.commanding.commands.Command
import com.fluxtah.askpluginsdk.logging.AskLogger
import com.fluxtah.askpluginsdk.logging.LogLevel
import kotlinx.coroutines.runBlocking
import org.jline.reader.LineReader
import org.jline.reader.LineReaderBuilder
import org.jline.terminal.Terminal
import org.jline.terminal.TerminalBuilder
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
    private val responsePrinter: AskResponsePrinter = AskConsoleResponsePrinter(),
    private val threadRepository: ThreadRepository = ThreadRepository(),
    private val commandFactory: CommandFactory = CommandFactory(
        logger,
        responsePrinter,
        assistantsApi,
        assistantRegistry,
        assistantInstallRepository,
        userProperties,
        threadRepository
    ),
    private val assistantRunner: AssistantRunner = AssistantRunner(
        logger = logger,
        assistantsApi = assistantsApi,
        assistantRegistry = assistantRegistry,
        assistantInstallRepository = assistantInstallRepository,
        functionInvoker = FunctionInvoker(),
    ),
) {
    private val completer = AskCommandCompleter(assistantRegistry, commandFactory, threadRepository)
    private val terminal: Terminal = TerminalBuilder.builder()
        .system(true)
        .build()
    private val lineReader: LineReader = LineReaderBuilder.builder()
        .terminal(terminal)
        .completer(completer)
        .build()

    init {
        val exposedLogger = org.jetbrains.exposed.sql.exposedLogger as Logger
        exposedLogger.level = Level.OFF

        userProperties.load()

        // assistantRegistry.register(FoodOrderingAssistant(logger))

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
            println()

            try {
                val prompt = if (userProperties.getAssistantId().isEmpty()) {
                    green("ask ➜")
                } else {
                    green("ask@${userProperties.getAssistantId()} ➜")
                }
                val input = lineReader.readLine("$prompt ")

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
            when {
                input.startsWith("/") -> {
                    val command = commandFactory.create(input)
                    if (runCommand(command)) return
                }

                input.startsWith(":") -> { // Alias for /exec
                    val command = commandFactory.create("/exec ${input.drop(1)}")
                    if (runCommand(command)) return
                }

                else -> {
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

                            responsePrinter.println()
                            val loadingChars = listOf("|", "/", "-", "\\")
                            var loadingCharIndex = 0

                            val truncationStrategy = if (userProperties.getTruncateLastMessages() > 0) {
                                TruncationStrategy.LastMessages(userProperties.getTruncateLastMessages())
                            } else {
                                null
                            }
                            val maxPromptTokens = if (userProperties.getMaxPromptTokens() > 0) {
                                userProperties.getMaxPromptTokens()
                            } else {
                                null
                            }
                            val maxCompletionTokens = if (userProperties.getMaxCompletionTokens() > 0) {
                                userProperties.getMaxCompletionTokens()
                            } else {
                                null
                            }
                            val result = assistantRunner.run(
                                details = RunDetails(
                                    assistantId = assistantId,
                                    threadId = currentThreadId,
                                    model = userProperties.getModel(),
                                    prompt = input,
                                    maxPromptTokens = maxPromptTokens,
                                    maxCompletionTokens = maxCompletionTokens,
                                    truncationStrategy = truncationStrategy
                                ),
                                onRunStatusChanged = { status ->
                                    loadingCharIndex = (loadingCharIndex + 1) % loadingChars.size

                                    when (status) {
                                        RunStatus.FAILED,
                                        RunStatus.CANCELLED,
                                        RunStatus.EXPIRED -> {
                                            responsePrinter.print("\u001b[1A\u001b[2K")
                                            responsePrinter.println(" x $status")
                                        }

                                        RunStatus.COMPLETED -> {
                                            responsePrinter.print("\u001b[1A\u001b[2K")
                                            responsePrinter.println(" ✔ $status")
                                        }

                                        else -> {
                                            responsePrinter.print("\u001b[1A\u001b[2K")
                                            responsePrinter.println(" ${loadingChars[loadingCharIndex]} $status")
                                        }
                                    }
                                },
                                onMessageCreation = { message ->
                                   // responsePrinter.println("QUX:" + message.content.joinToString(" ") { it.text.value })
                                    responsePrinter.println(message.content.joinToString(" ") { it.text.value })
                                    responsePrinter.println()
                                    responsePrinter.println()
                                }
                            )

                            when (result) {
                                is RunResult.Complete -> {
                                    userProperties.setRunId(result.runId)
                                    userProperties.setAssistantId(assistantId)
                                    userProperties.save()

                                    responsePrinter.println(result.responseText)
                                }

                                is RunResult.Error -> {
                                    responsePrinter.println(result.message)
                                }
                            }


                        }
                    }
                }
            }
        } catch (e: Exception) {
            responsePrinter.println("Error: ${e.message}, run with /log-level ERROR for more info")
            logger.log(LogLevel.ERROR, "Error: ${e.stackTraceToString()}")
        }
    }

    private fun runCommand(command: Command): Boolean {
        if (command.requiresApiKey) {
            if (userProperties.getOpenaiApiKey().isEmpty()) {
                responsePrinter.println("You need to set an OpenAI API key first! with /set-key <api-key>")
                return true
            }
        }
        runBlocking {
            command.execute()
        }
        return false
    }

    private fun printWelcomeMessage() {
        responsePrinter.println()
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
    }
}

