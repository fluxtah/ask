package com.fluxtah.ask.app.commanding.commands

import com.fluxtah.ask.app.audio.TextToSpeechPlayer

class PlayTalk(private val player: TextToSpeechPlayer) : Command() {
    override suspend fun execute() {
        player.playNext()
    }

    override val requiresApiKey: Boolean = false
}