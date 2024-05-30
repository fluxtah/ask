/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */
package com.fluxtah.ask.api.clients.openai.audio

import com.fluxtah.ask.api.clients.httpClient
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import java.io.File

data class CreateTranscriptionRequest(
    val audioFile: File,
    val model: String = "whisper-1",
    /**
     * The language of the input audio. Supplying the input language in ISO-639-1 format will improve accuracy and latency.
     */
    val language: String? = null,
    /**
     * An optional text to guide the model's style or continue a previous audio segment. The prompt should match the audio language.
     */
    val prompt: String? = null,

    /**
     * Defaults to json. The format of the transcript output, in one of these options: json, text, srt, verbose_json, or vtt.
     */
    val responseFormat: String? = null,

    /**
     * The sampling temperature, between 0 and 1. Higher values like 0.8 will make the output more random, while lower values like 0.2
     * will make it more focused and deterministic. If set to 0, the model will use log probability to
     * automatically increase the temperature until certain thresholds are hit.
     */
    val temperature: Double? = null,
)

@Serializable
data class CreateTranscriptionResponse(val text: String)

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
}