package com.fluxtah.ask.api.clients.openai.assistants.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SubmitToolOutputsRequest(
    @SerialName("tool_outputs") val toolOutputs: List<ToolOutput>
)