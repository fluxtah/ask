/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.api.clients.openai.assistants.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AssistantRunStep(
    @SerialName("id") val id: String,
    @SerialName("object") val objectName: String,
    @SerialName("created_at") val createdAt: Long,
    @SerialName("run_id") val runId: String,
    @SerialName("assistant_id") val assistantId: String,
    @SerialName("thread_id") val threadId: String,
    @SerialName("type") val type: String,
    @SerialName("status") val status: String,
    @SerialName("cancelled_at") val cancelledAt: Long? = null,
    @SerialName("completed_at") val completedAt: Long? = null,
    @SerialName("expired_at") val expiredAt: Long? = null,
    @SerialName("failed_at") val failedAt: Long? = null,
    @SerialName("last_error") val lastError: String? = null,
    @SerialName("step_details") val stepDetails: AssistantRunStepDetails
)