/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.app

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import com.fluxtah.ask.Version
import com.fluxtah.ask.api.UserProperties
import com.fluxtah.ask.api.ansi.green
import com.fluxtah.ask.api.ansi.printWhite
import com.fluxtah.ask.api.assistants.AssistantInstallRepository
import com.fluxtah.ask.api.assistants.AssistantRegistry
import com.fluxtah.ask.api.clients.openai.assistants.AssistantsApi
import com.fluxtah.ask.api.plugins.AskPluginLoader
import com.fluxtah.ask.api.printers.AskConsoleResponsePrinter
import com.fluxtah.ask.api.printers.AskResponsePrinter
import com.fluxtah.ask.api.store.PropertyStore
import com.fluxtah.ask.api.tools.fn.FunctionInvoker
import com.fluxtah.ask.app.commanding.CommandFactory
import com.fluxtah.ask.assistants.FoodOrderingAssistant
import com.fluxtah.ask.api.repository.ThreadRepository
import com.fluxtah.askpluginsdk.logging.AskLogger
import com.fluxtah.askpluginsdk.logging.LogLevel
import kotlinx.coroutines.runBlocking
import org.jline.reader.LineReader
import org.jline.reader.LineReaderBuilder
import org.jline.terminal.Terminal
import org.jline.terminal.TerminalBuilder
import org.slf4j.LoggerFactory
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
    private val assistantRunner: com.fluxtah.ask.api.AssistantRunner = com.fluxtah.ask.api.AssistantRunner(
        logger = logger,
        userProperties = userProperties,
        assistantsApi = assistantsApi,
        assistantRegistry = assistantRegistry,
        assistantInstallRepository = assistantInstallRepository,
        functionInvoker = FunctionInvoker(),
        responsePrinter = responsePrinter
    ),
) {
    val completer = AskCommandCompleter(assistantRegistry, commandFactory)
    val terminal: Terminal = TerminalBuilder.builder()
        .system(true)
        .build()
    val lineReader: LineReader = LineReaderBuilder.builder()
        .terminal(terminal)
        .completer(completer)
        .build()

    init {
        userProperties.load()

        assistantRegistry.register(FoodOrderingAssistant(logger))

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
        val loggerContext = LoggerFactory.getILoggerFactory()
        val exposedLogger = org.jetbrains.exposed.sql.exposedLogger as Logger
        exposedLogger.level = Level.OFF

        printWelcomeMessage()

        val logLevel = userProperties.getLogLevel()
        if (logLevel != LogLevel.OFF) {
            logger.log(LogLevel.DEBUG, "Log level: $logLevel")
        }

        logger.setLogLevel(logLevel)

        while (true) {
            println()

            try {
                val input = lineReader.readLine("${green("ask ➜")} ")
                printWhite()

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
        responsePrinter.println()
        responsePrinter.println("""
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

