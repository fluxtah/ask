package com.fluxtah.ask.app

import com.fluxtah.ask.api.AssistantRunManager
import com.fluxtah.ask.api.printers.AskResponsePrinter
import com.fluxtah.ask.api.store.user.UserProperties
import com.fluxtah.ask.app.commanding.CommandFactory
import com.fluxtah.ask.app.commanding.commands.Command
import com.fluxtah.askpluginsdk.logging.AskLogger
import com.fluxtah.askpluginsdk.logging.LogLevel
import kotlinx.coroutines.runBlocking

class InputHandler(
    private val commandFactory: CommandFactory,
    private val responsePrinter: AskResponsePrinter,
    private val logger: AskLogger,
    private val userProperties: UserProperties,
    private val assistantRunManager: AssistantRunManager,
) {
    fun handleInput(input: String) {
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
                    assistantRunManager.runAssistant(input)
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
}
