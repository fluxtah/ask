package com.fluxtah.ask.api.clients.openai.assistants.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RunRequest(
    @SerialName("assistant_id") val assistantId: String,
    @SerialName("model") val model: String? = null,
    @SerialName("instructions") val instructions: String? = null,
    @SerialName("tools") val tools: List<String>? = null,
    @SerialName("metadata") val metadata: Map<String, String>? = null
)