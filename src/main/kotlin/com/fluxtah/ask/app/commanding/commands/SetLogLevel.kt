package com.fluxtah.ask.app.commanding.commands

import com.fluxtah.ask.app.AskLogger
import com.fluxtah.ask.app.LogLevel
import com.fluxtah.ask.app.UserProperties

data class SetLogLevel(val userProperties: UserProperties, val askLogger: AskLogger, val logLevel: LogLevel) :
    Command() {
    override val requiresApiKey: Boolean = false
    override suspend fun execute() {
        userProperties.setLogLevel(logLevel)
        askLogger.setLogLevel(logLevel)
        userProperties.save()
    }
}