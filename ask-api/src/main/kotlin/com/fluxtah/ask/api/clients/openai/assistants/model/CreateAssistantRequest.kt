/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

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
    @SerialName("temperature") val temperature: Float? = null,
    @SerialName("top_p") val topP: Float? = null,

    /**
     * Specifies the format that the model must output. Compatible with GPT-4o, GPT-4 Turbo, and
     * all GPT-3.5 Turbo models since gpt-3.5-turbo-1106.
     *
     * Setting to { "type": "json_object" } enables JSON mode, which guarantees the message the
     * model generates is valid JSON.
     *
     * Important: when using JSON mode, you must also instruct the model to produce JSON
     * yourself via a system or user message. Without this, the model may generate an unending
     * stream of whitespace until the generation reaches the token limit, resulting in a
     * long-running and seemingly "stuck" request. Also note that the message content may be
     * partially cut off if finish_reason="length", which indicates the generation
     * exceeded max_tokens or the conversation exceeded the max context length.
     */
    @SerialName("response_format") val responseFormat: ResponseFormat? = null
)

@Serializable
data class ResponseFormat(
    @SerialName("type") val type: String,
) {
    companion object {
        val JSON = ResponseFormat("json_object")
    }
}

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