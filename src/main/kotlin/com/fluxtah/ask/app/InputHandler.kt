package com.fluxtah.ask.app

import com.fluxtah.ask.api.AssistantRunManager
import com.fluxtah.ask.api.printers.AskResponsePrinter
import com.fluxtah.ask.api.store.user.UserProperties
import com.fluxtah.ask.app.commanding.CommandFactory
import com.fluxtah.askpluginsdk.logging.AskLogger
import com.fluxtah.askpluginsdk.logging.LogLevel

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
                    commandFactory.executeCommand(input)
                }

                input.startsWith(":") -> { // Alias for /exec
                    commandFactory.executeCommand("/exec ${input.drop(1)}")
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

}
