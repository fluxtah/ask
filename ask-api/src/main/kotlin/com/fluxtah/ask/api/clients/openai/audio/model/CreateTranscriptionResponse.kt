package com.fluxtah.ask.api.clients.openai.audio.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateTranscriptionResponse(val text: String)