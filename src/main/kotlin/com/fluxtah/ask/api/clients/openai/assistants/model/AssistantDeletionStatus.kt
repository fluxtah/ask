package com.fluxtah.ask.api.clients.openai.assistants.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AssistantDeletionStatus(
    val id: String,
    @SerialName("object") val objectName: String,
    val deleted: Boolean = false
)