/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.api.clients.openai.assistants.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Message(
    @SerialName("id") val id: String,
    @SerialName("object") val objectName: String,
    @SerialName("created_at") val createdAt: Long,
    @SerialName("thread_id") val threadId: String,
    @SerialName("role") val role: String,
    @SerialName("content") val content: List<AssistantMessageContent>,
    @SerialName("file_ids") val fileIds: List<String> = emptyList(),
    @SerialName("assistant_id") val assistantId: String? = null,
    @SerialName("run_id") val runId: String? = null,
    @SerialName("metadata") val metadata: Map<String, String> = emptyMap()
)