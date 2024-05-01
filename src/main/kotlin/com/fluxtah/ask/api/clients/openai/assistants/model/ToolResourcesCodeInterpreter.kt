package com.fluxtah.ask.api.clients.openai.assistants.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ToolResourcesCodeInterpreter(
    @SerialName("file_ids") val fileIds: List<String> = emptyList(),
)