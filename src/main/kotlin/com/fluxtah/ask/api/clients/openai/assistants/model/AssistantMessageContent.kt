package com.fluxtah.ask.api.clients.openai.assistants.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AssistantMessageContent(
    @SerialName("type") val type: String,
    @SerialName("text") val text: AssistantMessageText
)