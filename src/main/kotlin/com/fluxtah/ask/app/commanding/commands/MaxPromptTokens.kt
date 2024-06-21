package com.fluxtah.ask.app.commanding.commands

import com.fluxtah.ask.api.printers.AskResponsePrinter
import com.fluxtah.ask.api.store.user.UserProperties

class MaxPromptTokens(
    private val userProperties: UserProperties,
    private val responsePrinter: AskResponsePrinter
) : Command() {
    override val requiresApiKey: Boolean = false

    override suspend fun execute(args: List<String>) {
        if (args.size != 1 || args.first().toIntOrNull() == null) {
            responsePrinter.println("Current max prompt tokens: ${userProperties.getMaxPromptTokens()}, to set a new value use /max-prompt-tokens <number>")
        } else {
            val maxPromptTokens = args.first().toInt()
            userProperties.setMaxPromptTokens(maxPromptTokens)
        }
    }
}
