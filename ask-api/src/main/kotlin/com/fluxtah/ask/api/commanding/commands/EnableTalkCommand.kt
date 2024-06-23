package com.fluxtah.ask.api.commanding.commands

import com.fluxtah.ask.api.printers.AskResponsePrinter
import com.fluxtah.ask.api.store.user.UserProperties
import com.fluxtah.ask.api.audio.TextToSpeechPlayer

class EnableTalkCommand(
    private val userProperties: UserProperties,
    private val responsePrinter: AskResponsePrinter,
    private val textToSpeechPlayer: TextToSpeechPlayer
) : Command() {
    override val requiresApiKey: Boolean = false

    override suspend fun execute(args: List<String>) {
        val enable = !userProperties.getTalkEnabled()
        userProperties.setTalkEnabled(enable)
        textToSpeechPlayer.enabled = enable
        responsePrinter.printMessage("Talk mode is now ${if (enable) "enabled" else "disabled"}.")
        if (!enable) {
            textToSpeechPlayer.stop()
        }
    }
}