package com.fluxtah.ask.api.clients.openai.assistants.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class AssistantTool {

    @Serializable
    @SerialName("function")
    data class FunctionTool(
        @SerialName("function") val function: FunctionSpec
    ) : AssistantTool() {
        @Serializable
        data class FunctionSpec(
            val name: String,
            val description: String? = null,
            val parameters: ParametersSpec = ParametersSpec()
        )

        @Serializable
        data class ParametersSpec(
            val type: String? = null,
            val properties: Map<String, PropertySpec> = emptyMap(),
            val required: List<String> = emptyList()
        )

        @Serializable
        data class PropertySpec(
            val type: String,
            val description: String,
        )
    }

    @Serializable
    @SerialName("code_interpreter")
    data object CodeInterpreter : AssistantTool()

    @Serializable
    @SerialName("retrieval")
    data object Retrieval : AssistantTool()
}
