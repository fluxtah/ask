/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.api.clients.openai.assistants.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RunRequest(
    /**
     * The ID of the assistant to use to execute this run.
     */
    @SerialName("assistant_id") val assistantId: String,

    /**
     * The ID of the Model to be used to execute this run.
     * If a value is provided here, it will override the model associated with the assistant.
     * If not, the model associated with the assistant will be used.
     */
    @SerialName("model") val model: String? = null,

    /**
     * Overrides the instructions of the assistant. This is useful for modifying the behavior on a per-run basis.
     */
    @SerialName("instructions") val instructions: String? = null,

    /**
     * Appends additional instructions at the end of the instructions for the run.
     * This is useful for modifying the behavior on a per-run basis without overriding other instructions.
     */
    @SerialName("additional_instructions") val additionalInstructions: String? = null,

    /**
     * Adds additional messages to the thread before creating the run.
     */
    @SerialName("additional_messages") val additionalMessages: List<Message>? = null,

    /**
     * Override the tools the assistant can use for this run.
     * This is useful for modifying the behavior on a per-run basis.
     */
    @SerialName("tools") val tools: List<AssistantTool>? = null,

    /**
     * Set of 16 key-value pairs that can be attached to an object.
     * This can be useful for storing additional information about the object in a structured format.
     * Keys can be a maximum of 64 characters long and values can be a maximum of 512 characters long.
     */
    @SerialName("metadata") val metadata: Map<String, String>? = null,

    /**
     * What sampling temperature to use, between 0 and 2. Higher values like 0.8 will make the output more random,
     * while lower values like 0.2 will make it more focused and deterministic.
     */
    @SerialName("temperature") val temperature: Float? = null,

    /**
     * An alternative to sampling with temperature, called nucleus sampling, where the model considers
     * the results of the tokens with top_p probability mass. So 0.1 means only the tokens comprising
     * the top 10% probability mass are considered.
     *
     * We generally recommend altering this or temperature but not both.
     */
    @SerialName("top_p") val topP: Float? = null,

    /**
     * The maximum number of prompt tokens that may be used over the course of the run.
     * The run will make a best effort to use only the number of prompt tokens specified,
     * across multiple turns of the run. If the run exceeds the number of prompt tokens specified,
     * the run will end with status incomplete. See incomplete_details for more info.
     */
    @SerialName("max_prompt_tokens") val maxPromptTokens: Int? = null,

    /**
     * The maximum number of completion tokens that may be used over the course of the run.
     * The run will make a best effort to use only the number of completion tokens specified,
     * across multiple turns of the run. If the run exceeds the number of completion tokens specified,
     * the run will end with status incomplete. See incomplete_details for more info.
     */
    @SerialName("max_completion_tokens") val maxCompletionTokens: Int? = null,

    /**
     * Controls for how a thread will be truncated prior to the run.
     * Use this to control the initial context window of the run.
     */
    @SerialName("truncation_strategy") val truncationStrategy: TruncationStrategy? = null,

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

/**
 * The truncation strategy to use for the thread. The default is auto.
 * If set to last_messages, the thread will be truncated to the n most recent messages in the thread.
 * When set to auto, messages in the middle of the thread will be dropped to fit
 * the context length of the model, max_prompt_tokens.
 */
@Serializable
sealed class TruncationStrategy {
    @Serializable
    @SerialName("auto")
    data object Auto : TruncationStrategy()

    @Serializable
    @SerialName("last_messages")
    data class LastMessages(
        /**
         * The number of most recent messages from the thread when constructing the context for the run.
         */
        @SerialName("last_messages") val numMessages: Int? = null,
    ) : TruncationStrategy()
}