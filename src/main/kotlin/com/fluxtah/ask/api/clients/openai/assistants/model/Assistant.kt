package com.fluxtah.ask.api.clients.openai.assistants.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Assistant(
    @SerialName("id") val id: String,
    @SerialName("object") val objectName: String,
    @SerialName("created_at") val createdAt: Long,
    @SerialName("name") val name: String? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("model") val model: String? = null,
    @SerialName("instructions") val instructions: String? = null,
    @SerialName("tools") val tools: List<AssistantTool> = emptyList(),
    @SerialName("tool_resources") val toolResource: ToolResources? = null,
    @SerialName("metadata") val metadata: Map<String, String> = emptyMap()
)

@Serializable
data class ToolResources(
    @SerialName("code_interpreter") val codeInterpreter: ToolResourcesCodeInterpreter? = null,
    @SerialName("file_search") val fileSearch: ToolResourcesFileSearch? = null,
)

@Serializable
data class ToolResourcesCodeInterpreter(
    @SerialName("file_ids") val fileIds: List<String> = emptyList(),
)

@Serializable
data class ToolResourcesFileSearch(
    @SerialName("vector_store_ids") val vectorStoreIds: List<String> = emptyList(),
)

