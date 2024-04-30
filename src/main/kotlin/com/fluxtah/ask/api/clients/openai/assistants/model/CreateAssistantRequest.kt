package com.fluxtah.ask.api.clients.openai.assistants.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateAssistantRequest(
    @SerialName("model") val model: String? = null,
    @SerialName("name") val name: String? =  null,
    @SerialName("description") val description: String? = null,
    @SerialName("instructions") val instructions: String? = null,
    @SerialName("tools") val tools: List<AssistantTool> = emptyList(),
    @SerialName("file_ids") val fileIds: List<String> = emptyList(),
    @SerialName("metadata") val metadata: Map<String, String> = emptyMap()
)