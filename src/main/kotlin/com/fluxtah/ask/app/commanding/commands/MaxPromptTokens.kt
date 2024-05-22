package com.fluxtah.ask.app.commanding.commands

import com.fluxtah.ask.app.UserProperties
import com.fluxtah.askpluginsdk.logging.AskLogger

class MaxPromptTokens(
    private val userProperties: UserProperties,
    private val maxPromptTokens: Int
) : Command() {
    override val requiresApiKey: Boolean = false

    override suspend fun execute() {
        userProperties.setMaxPromptTokens(maxPromptTokens)
    }
}
