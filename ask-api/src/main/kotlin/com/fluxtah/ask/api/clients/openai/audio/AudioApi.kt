/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */
package com.fluxtah.ask.api.clients.openai.audio

import com.fluxtah.ask.api.clients.httpClient
import com.fluxtah.ask.api.clients.openai.audio.model.CreateTranscriptionRequest
import com.fluxtah.ask.api.clients.openai.audio.model.CreateTranscriptionResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class AudioApi(
    private val client: HttpClient = httpClient,
    private val baseUri: String = "https://api.openai.com",
    private val version: String = "v1",
    private val apiKeyProvider: () -> String
) {
    suspend fun createTranscription(request: CreateTranscriptionRequest): CreateTranscriptionResponse {
        val response = client.post("$baseUri/$version/audio/transcriptions") {
            header("Authorization", "Bearer ${apiKeyProvider.invoke()}")
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("model", request.model)
                        if (request.language != null) {
                            append("language", request.language)
                        }
                        if (request.prompt != null) {
                            append("prompt", request.prompt)
                        }
                        if (request.responseFormat != null) {
                            append("response_format", request.responseFormat)
                        }
                        if (request.temperature != null) {
                            append("temperature", request.temperature.toString())
                        }
                        append("file", request.audioFile.readBytes(), Headers.build {
                            append(
                                HttpHeaders.ContentDisposition,
                                "form-data; name=\"file\"; filename=\"${request.audioFile.name}\""
                            )
                            append(HttpHeaders.ContentType, "audio/wav")
                        })
                    }
                )
            )
        }

        when (response.status) {
            HttpStatusCode.OK -> {
                return response.body()
            }

            else -> throw IllegalStateException(response.bodyAsText())
        }
    }

    /**
     * Generates audio from the input text.
     */
    suspend fun createSpeech(request: CreateSpeechRequest): ByteArray {
        val response = client.post("$baseUri/$version/audio/speech") {
            header("Authorization", "Bearer ${apiKeyProvider.invoke()}")
            contentType(ContentType.Application.Json)
            setBody<CreateSpeechRequest>(request)
        }

        when (response.status) {
            HttpStatusCode.OK -> {
                return response.body()
            }

            else -> throw IllegalStateException(response.bodyAsText())
        }
    }
}

@Serializable
data class CreateSpeechRequest(
    /**
     * One of the available TTS models: tts-1 or tts-1-hd
     */
    @SerialName("model")
    val model: SpeechModel,
    /**
     * The text to generate audio for. The maximum length is 4096 characters.
     */
    val input: String,
    /**
     * The voice to use when generating the audio. Supported voices are alloy, echo, fable, onyx, nova, and shimmer.
     * Previews of the voices are available in the Text to speech guide https://docs.openai.com/text-to-speech/overview/
     */
    val voice: SpeechVoice,

    /**
     * The format to audio in. Supported formats are mp3, opus, aac, flac, wav, and pcm.
     */
    @SerialName("response_format")
    val responseFormat: ResponseFormat? = null,
    /**
     * The speed of the generated audio. Select a value from 0.25 to 4.0. 1.0 is the default.
     */
    val speed: Double? = null
)

@Serializable
enum class SpeechModel {
    @SerialName("tts-1")
    TTS_1,
    @SerialName("tts-1-hd")
    TTS_1_HD
}

@Serializable
enum class SpeechVoice {
    @SerialName("alloy")
    ALLOY,
    @SerialName("echo")
    ECHO,
    @SerialName("fable")
    FABLE,
    @SerialName("onyx")
    ONYX,
    @SerialName("nova")
    NOVA,
    @SerialName("shimmer")
    SHIMMER
}

@Serializable
enum class ResponseFormat {
    @SerialName("mp3")
    MP3,
    @SerialName("opus")
    OPUS,
    @SerialName("aac")
    AAC,
    @SerialName("flac")
    FLAC,
    @SerialName("wav")
    WAV,
    @SerialName("pcm")
    PCM
}