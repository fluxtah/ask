package com.fluxtah.ask.api.clients.openai.assistants.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ToolResourcesFileSearch(
    @SerialName("vector_store_ids") val vectorStoreIds: List<String> = emptyList(),
)