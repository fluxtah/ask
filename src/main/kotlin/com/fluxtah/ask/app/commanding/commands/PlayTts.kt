package com.fluxtah.ask.app.commanding.commands

import com.fluxtah.ask.api.audio.TextToSpeechPlayer

class PlayTts(private val player: TextToSpeechPlayer) : Command() {
    override suspend fun execute() {
        player.playNext()
    }

    override val requiresApiKey: Boolean = false
}