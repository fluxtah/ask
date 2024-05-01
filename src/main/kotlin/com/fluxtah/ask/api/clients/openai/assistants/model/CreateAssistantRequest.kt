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
    @SerialName("tool_resources") val toolResource: ToolResources? = null,
    @SerialName("metadata") val metadata: Map<String, String> = emptyMap(),
    @SerialName("temperature") val temperature: Float? = null
)

@Serializable
data class ModifyAssistantRequest(
    @SerialName("model") val model: String? = null,
    @SerialName("name") val name: String? =  null,
    @SerialName("description") val description: String? = null,
    @SerialName("instructions") val instructions: String? = null,
    @SerialName("tools") val tools: List<AssistantTool> = emptyList(),
    @SerialName("tool_resources") val toolResource: ToolResources? = null,
    @SerialName("metadata") val metadata: Map<String, String> = emptyMap(),
    @SerialName("temperature") val temperature: Float? = null
)