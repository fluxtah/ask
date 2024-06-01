/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.app.commanding.commands

import com.fluxtah.ask.app.audio.TextToSpeechPlayer

class SkipTalk(private val player: TextToSpeechPlayer) : Command() {
    override suspend fun execute() {
        player.skipNext()
        player.playNext()
    }

    override val requiresApiKey: Boolean = false
}
