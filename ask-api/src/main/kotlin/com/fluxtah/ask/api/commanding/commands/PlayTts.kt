package com.fluxtah.ask.api.commanding.commands

import com.fluxtah.ask.api.audio.TextToSpeechPlayer

class PlayTts(private val player: TextToSpeechPlayer) : Command() {
    override suspend fun execute(args: List<String>) {
        player.stop()
        player.playNext()
    }

    override val requiresApiKey: Boolean = false
}