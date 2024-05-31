package com.fluxtah.ask.app.audio

import AudioPlayer
import com.fluxtah.ask.api.clients.openai.audio.AudioApi
import com.fluxtah.ask.api.clients.openai.audio.CreateSpeechRequest
import com.fluxtah.ask.api.clients.openai.audio.ResponseFormat
import com.fluxtah.ask.api.clients.openai.audio.SpeechModel
import com.fluxtah.ask.api.clients.openai.audio.SpeechVoice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class TextToSpeechPlayer(
    private val audioApi: AudioApi,
    private val audioPlayer: AudioPlayer,
    private val coroutineScope: CoroutineScope,
) {
    fun playText(text: String) {
        coroutineScope.launch {
            val audio = audioApi.createSpeech(
                CreateSpeechRequest(
                    model = SpeechModel.TTS_1,
                    voice = SpeechVoice.ECHO,
                    responseFormat = ResponseFormat.WAV,
                    input = text,
                    speed = 1.0
                )
            )
            audioPlayer.play(audio)
        }
    }
}