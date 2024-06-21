/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.api.commanding.commands

import com.fluxtah.ask.api.printers.AskResponsePrinter
import com.fluxtah.ask.api.store.user.UserProperties

class SetOpenAiApiKey(
    private val userProperties: UserProperties,
    private val responsePrinter: AskResponsePrinter
) : Command() {
    override val requiresApiKey: Boolean = false
    override suspend fun execute(args: List<String>) {
        if (args.size != 1) {
            responsePrinter.println("Invalid number of arguments for /set-key, expected an API key following the command")
            return
        }
        val apiKey = args[0]
        userProperties.setOpenAiApiKey(apiKey)
        userProperties.save()
    }
}