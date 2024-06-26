package com.fluxtah.ask.api.audio

import AudioPlayer
import com.fluxtah.ask.api.clients.openai.audio.AudioApi
import com.fluxtah.ask.api.clients.openai.audio.CreateSpeechRequest
import com.fluxtah.ask.api.clients.openai.audio.ResponseFormat
import com.fluxtah.ask.api.clients.openai.audio.SpeechModel
import com.fluxtah.ask.api.clients.openai.audio.SpeechVoice
import com.fluxtah.ask.api.markdown.MarkdownParser
import com.fluxtah.ask.api.markdown.Token
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

const val ADVICE_PLAY_OR_SKIP_CODE = "Type SLASH P to play the code block or SLASH S to skip it."

class TextToSpeechPlayer(
    private val audioApi: AudioApi,
    private val audioPlayer: AudioPlayer,
    private val coroutineScope: CoroutineScope,
) {
    // Backing property, mutable and private
    private val _ttsSegments = mutableListOf<TtsSegment>()

    // Public property, immutable view
    val ttsSegments: List<TtsSegment>
        get() = _ttsSegments

    var enabled: Boolean = true

    fun queue(text: String) {
        if (!enabled) {
            return
        }
        val markdownParser = MarkdownParser(text)
        val tokens = markdownParser.parse()
        val builder = StringBuilder()
        val newSegments = mutableListOf<TtsSegment>()

        tokens.forEach { token ->
            when (token) {
                is Token.CodeBlock -> {
                    appendBlock(builder, newSegments, ADVICE_PLAY_OR_SKIP_CODE)
                    if (builder.isNotEmpty()) {
                        newSegments.add(TtsSegment.Text(builder.toString()))
                        builder.clear()
                    }
                    newSegments.add(TtsSegment.CodeBlock(token.language, token.content))
                }

                is Token.Bold -> {
                    appendBlock(builder, newSegments, token.content)
                }

                is Token.Code -> {
                    appendBlock(builder, newSegments, token.content)
                }

                is Token.Text -> {
                    appendBlock(builder, newSegments, token.content)
                }
            }
        }

        if (builder.isNotEmpty()) {
            newSegments.add(TtsSegment.Text(builder.toString()))
            builder.clear()
        }

        // Autoplay the first text segment
        if (newSegments.first() is TtsSegment.Text) {
            newSegments[0] = (newSegments.first() as TtsSegment.Text).copy(autoPlay = true)
        }

        // Autoplay text blocks after code blocks
        newSegments.forEachIndexed { index, ttsSegment ->
            if (ttsSegment is TtsSegment.CodeBlock) {
                val nextIndex = index + 1
                if (nextIndex < newSegments.size && newSegments[nextIndex] is TtsSegment.Text) {
                    newSegments[nextIndex] = (newSegments[nextIndex] as TtsSegment.Text).copy(autoPlay = true)
                }
            }
        }

        _ttsSegments.addAll(newSegments)
    }

    private fun appendBlock(builder: StringBuilder, segments: MutableList<TtsSegment>, content: String) {
        if ((builder.count() + content.count()) > 4096) {
            val breakSymbols = listOf('.', '!', '?')
            val nearestSymbol = builder.indexOfLast { it in breakSymbols }
            if (nearestSymbol != -1) {
                segments.add(TtsSegment.Text(builder.substring(0, nearestSymbol + 1)))
                val remaining = builder.substring(nearestSymbol + 1)
                builder.clear()
                builder.append(remaining)
            } else {
                segments.add(TtsSegment.Text(builder.toString()))
                builder.clear()
            }
        } else {
            builder.append(content)
        }
    }

    fun skipNext() {
        ttsSegments.firstOrNull()?.let {
            _ttsSegments.removeAt(0)
        }
    }

    fun playNext() {
        if (!enabled) {
            return
        }

        // Don't play if already playing
        if(audioPlayer.isPlaying()) {
            return
        }

        ttsSegments.firstOrNull()?.let { segment ->
            when (segment) {
                is TtsSegment.Text -> playText(segment.content, onComplete = {
                    if (ttsSegments.firstOrNull()?.autoPlay == true) {
                        playNext()
                    }
                })

                is TtsSegment.CodeBlock -> playText(segment.content, onComplete = {
                    if (ttsSegments.firstOrNull()?.autoPlay == true) {
                        playNext()
                    }
                })
            }
            if(ttsSegments.isNotEmpty()) {
                _ttsSegments.removeAt(0)
            }
        }
    }

    private fun playText(text: String, onComplete: () -> Unit = {}) {
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
            audioPlayer.play(audio, onComplete)
        }
    }

    fun stop() {
        audioPlayer.stop()
    }

    fun clear() {
        _ttsSegments.clear()
    }

    sealed class TtsSegment {
        abstract val autoPlay: Boolean

        data class Text(val content: String, override val autoPlay: Boolean = false) : TtsSegment()
        data class CodeBlock(val language: String?, val content: String, override val autoPlay: Boolean = false) :
            TtsSegment()
    }
}
