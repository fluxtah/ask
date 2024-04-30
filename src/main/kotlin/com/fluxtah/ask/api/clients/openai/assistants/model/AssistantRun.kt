package com.fluxtah.ask.api.clients.openai.assistants.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AssistantRun(
    @SerialName("id") val id: String,
    @SerialName("object") val objectName: String,
    @SerialName("created_at") val createdAt: Long,
    @SerialName("assistant_id") val assistantId: String,
    @SerialName("thread_id") val threadId: String,
    @SerialName("status") val status: RunStatus,
    @SerialName("started_at") val startedAt: Long? = null,
    @SerialName("expires_at") val expiresAt: Long? = null,
    @SerialName("cancelled_at") val cancelledAt: Long? = null,
    @SerialName("failed_at") val failedAt: Long? = null,
    @SerialName("completed_at") val completedAt: Long? = null,
    @SerialName("last_error") val lastError: AssistantRunError? = null,
    @SerialName("model") val model: String,
    @SerialName("instructions") val instructions: String? = null,
    @SerialName("tools") val tools: List<AssistantTool> = emptyList(),
    @SerialName("file_ids") val fileIds: List<String> = emptyList(),
    @SerialName("metadata") val metadata: Map<String, String> = emptyMap()
)

