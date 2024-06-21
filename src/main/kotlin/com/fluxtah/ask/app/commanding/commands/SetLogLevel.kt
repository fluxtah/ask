package com.fluxtah.ask.app.commanding.commands

import com.fluxtah.ask.api.printers.AskResponsePrinter
import com.fluxtah.ask.api.store.user.UserProperties
import com.fluxtah.askpluginsdk.logging.AskLogger
import com.fluxtah.askpluginsdk.logging.LogLevel

data class SetLogLevel(
    val userProperties: UserProperties,
    val askLogger: AskLogger,
    val responsePrinter: AskResponsePrinter
) : Command() {
    override val requiresApiKey: Boolean = false
    override suspend fun execute(args: List<String>) {
        if (args.size != 1) {
            responsePrinter.println("Invalid number of arguments for /log-level, expected a log level ERROR, DEBUG, INFO or OFF following the command, current log level: ${userProperties.getLogLevel()}")
            return
        }

        try {
            val logLevel = LogLevel.valueOf(args.first())
            userProperties.setLogLevel(logLevel)
            askLogger.setLogLevel(logLevel)
            userProperties.save()
        } catch (e: IllegalArgumentException) {
            responsePrinter.println("Invalid log level: ${args.first()}")
        }
    }
}