/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.api.commanding.commands

import com.fluxtah.ask.api.store.user.UserProperties
import com.fluxtah.ask.api.printers.AskResponsePrinter

class VoiceAutoSendCommand(
    private val userProperties: UserProperties,
    private val responsePrinter: AskResponsePrinter
) : Command() {
    override val requiresApiKey: Boolean = false

    override suspend fun execute(args: List<String>) {
        val enabled = userProperties.getAutoSendVoice()
        userProperties.setAutoSendVoice(!enabled)
        responsePrinter.println("Voice auto-send mode is now ${if (!enabled) "enabled" else "disabled"}.")
    }
}

