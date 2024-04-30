package com.fluxtah.ask.api.clients.openai.assistants.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ToolOutput(
    @SerialName("tool_call_id") val toolCallId: String,
    @SerialName("output") val output: String? = null
)