package com.fluxtah.ask.api.commanding.commands

import com.fluxtah.ask.api.printers.AskResponsePrinter
import com.fluxtah.ask.api.store.user.UserProperties

class MaxCompletionTokens(
    private val userProperties: UserProperties,
    private val responsePrinter: AskResponsePrinter
) : Command() {
    override val requiresApiKey: Boolean = false

    override suspend fun execute(args: List<String>) {
        if (args.size != 1 || args.first().toIntOrNull() == null) {
            responsePrinter
                .printMessage("Current max completion tokens: ${userProperties.getMaxCompletionTokens()}, to set a new value use /max-completion-tokens <number>")
        } else {
            val maxCompletionTokens = args.first().toInt()
            userProperties.setMaxCompletionTokens(maxCompletionTokens)
        }
    }
}
