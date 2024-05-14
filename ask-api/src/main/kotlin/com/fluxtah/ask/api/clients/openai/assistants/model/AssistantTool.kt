/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.api.clients.openai.assistants.model

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
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
            @EncodeDefault(EncodeDefault.Mode.NEVER)
            val description: String = "",
            val parameters: ParametersSpec = ParametersSpec()
        )

        @Serializable
        data class ParametersSpec(
            val type: String? = null,
            val properties: Map<String, PropertySpec> = emptyMap(),
            val required: List<String> = emptyList()
        )

        @Serializable
        data class PropertySpec @OptIn(ExperimentalSerializationApi::class) constructor(
            val type: String,
            @EncodeDefault(EncodeDefault.Mode.NEVER)
            val description: String = "",
            @EncodeDefault(EncodeDefault.Mode.NEVER)
            val properties: Map<String, PropertySpec> = emptyMap()
        )
    }

    @Serializable
    @SerialName("code_interpreter")
    data object CodeInterpreter : AssistantTool()

    @Serializable
    @SerialName("retrieval")
    data object Retrieval : AssistantTool()
}
