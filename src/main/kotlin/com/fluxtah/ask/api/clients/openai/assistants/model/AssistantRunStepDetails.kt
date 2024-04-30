package com.fluxtah.ask.api.clients.openai.assistants.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class AssistantRunStepDetails {
    @Serializable
    @SerialName("message_creation")
    data class MessageCreation(
        @SerialName("message_creation") val messageCreation: MessageCreationDetails
    ) : AssistantRunStepDetails() {
        @Serializable
        data class MessageCreationDetails(
            @SerialName("message_id") val messageId: String
        )
    }

    @Serializable
    @SerialName("tool_calls")
    data class ToolCalls(
        @SerialName("tool_calls") val toolCalls: List<ToolCallDetails>
    ) : AssistantRunStepDetails() {

        @Serializable
        sealed class ToolCallDetails {
            @Serializable
            @SerialName("function")
            data class FunctionToolCallDetails(
                @SerialName("id") val id: String,
                @SerialName("function") val function: FunctionSpec
            ) : ToolCallDetails() {
                @Serializable
                data class FunctionSpec(
                    @SerialName("name") val name: String,
                    @SerialName("arguments") val arguments: String
                )
            }
        }
    }
}