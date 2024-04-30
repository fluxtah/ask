package com.fluxtah.ask.api.clients.openai.assistants.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AssistantMessageList(
    @SerialName("object") val objectName: String,
    @SerialName("data") val data: List<Message> = emptyList(),
    @SerialName("first_id") val firstId: String? = null,
    @SerialName("last_id") val lastId: String? = null,
    @SerialName("has_more") val hasMore: Boolean = false
)