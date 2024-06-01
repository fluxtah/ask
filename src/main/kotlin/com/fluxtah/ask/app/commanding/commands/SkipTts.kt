/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.app.commanding.commands

import com.fluxtah.ask.api.audio.AudioRecorder
import com.fluxtah.ask.api.audio.TextToSpeechPlayer
import com.fluxtah.ask.api.printers.AskResponsePrinter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class SkipTts(private val player: TextToSpeechPlayer) : Command() {
    override suspend fun execute() {
        player.skipNext()
        player.playNext()
    }

    override val requiresApiKey: Boolean = false
}

class RecordVoice(
    private val coroutineScope: CoroutineScope,
    private val audioRecorder: AudioRecorder,
    private val responsePrinter: AskResponsePrinter
) : Command() {
    override suspend fun execute() {
        coroutineScope.launch {
            if (audioRecorder.isRecording()) {
                audioRecorder.stop()
            } else {
                audioRecorder.start()
            }
        }
        responsePrinter.print("\u001b[1A\u001b[2K")
        Thread.sleep(100)
    }

    override val requiresApiKey: Boolean = false
}
