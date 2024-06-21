/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.app.commanding.commands

import com.fluxtah.ask.api.audio.TextToSpeechPlayer

class SkipTts(private val player: TextToSpeechPlayer) : Command() {
    override suspend fun execute(args: List<String>) {
        player.stop()
        player.skipNext()
        player.playNext()
    }

    override val requiresApiKey: Boolean = false
}

