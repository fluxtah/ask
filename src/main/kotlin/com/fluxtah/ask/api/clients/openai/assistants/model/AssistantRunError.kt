package com.fluxtah.ask.api.clients.openai.assistants.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AssistantRunError(
    @SerialName("code") val code: String,
    @SerialName("message") val message: String
)