package com.fluxtah.ask.app.commanding.commands

import com.fluxtah.ask.app.UserProperties

class MaxCompletionTokens(
    private val userProperties: UserProperties,
    private val maxCompletionTokens: Int
) : Command() {
    override val requiresApiKey: Boolean = false

    override suspend fun execute() {
        userProperties.setMaxCompletionTokens(maxCompletionTokens)
    }
}
