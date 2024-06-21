package com.fluxtah.ask.api.commanding.commands

import com.fluxtah.ask.api.audio.AudioRecorder
import com.fluxtah.ask.api.audio.TextToSpeechPlayer
import com.fluxtah.ask.api.printers.AskResponsePrinter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class RecordVoice(
    private val coroutineScope: CoroutineScope,
    private val audioRecorder: AudioRecorder,
    private val responsePrinter: AskResponsePrinter,
    private val player: TextToSpeechPlayer
) : Command() {
    override suspend fun execute(args: List<String>) {
        coroutineScope.launch {
            player.stop()
            audioRecorder.start()
        }
        responsePrinter.print("\u001b[1A\u001b[2K")
        // Unfortunate hack to allow the audio recorder to start/stop prevents a race condition
        Thread.sleep(250)
    }

    override val requiresApiKey: Boolean = false
}