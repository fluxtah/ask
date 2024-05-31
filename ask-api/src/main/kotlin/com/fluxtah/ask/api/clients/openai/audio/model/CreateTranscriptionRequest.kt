package com.fluxtah.ask.api.clients.openai.audio.model

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