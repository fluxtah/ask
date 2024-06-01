package com.fluxtah.ask.app.commanding.commands

import com.fluxtah.ask.api.printers.AskResponsePrinter
import com.fluxtah.ask.api.store.user.UserProperties
import com.fluxtah.ask.app.audio.TextToSpeechPlayer

class EnableTalkCommand(
    private val userProperties: UserProperties,
    private val responsePrinter: AskResponsePrinter,
    private val textToSpeechPlayer: TextToSpeechPlayer
) : Command() {
    override val requiresApiKey: Boolean = false

    override suspend fun execute() {
        val enabled = userProperties.getTalkEnabled()
        userProperties.setTalkEnabled(enabled)
        responsePrinter.println("Talk mode is now ${if (!enabled) "enabled" else "disabled"}.")
        if(enabled) {
            textToSpeechPlayer.stop()
        }
    }
}