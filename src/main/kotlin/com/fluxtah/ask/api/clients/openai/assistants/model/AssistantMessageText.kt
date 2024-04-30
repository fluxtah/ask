package com.fluxtah.ask.api.clients.openai.assistants.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AssistantMessageText(
    @SerialName("value") val value: String,
    @SerialName("annotations") val annotations: List<String>
)