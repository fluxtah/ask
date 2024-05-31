package com.fluxtah.ask.app.commanding.commands

import com.fluxtah.ask.api.store.user.UserProperties
import com.fluxtah.askpluginsdk.logging.AskLogger
import com.fluxtah.askpluginsdk.logging.LogLevel

data class SetLogLevel(val userProperties: UserProperties, val askLogger: AskLogger, val logLevel: LogLevel) :
    Command() {
    override val requiresApiKey: Boolean = false
    override suspend fun execute() {
        userProperties.setLogLevel(logLevel)
        askLogger.setLogLevel(logLevel)
        userProperties.save()
    }
}