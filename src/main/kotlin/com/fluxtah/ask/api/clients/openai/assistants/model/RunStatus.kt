package com.fluxtah.ask.api.clients.openai.assistants.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class RunStatus {
    @SerialName("queued")
    QUEUED,

    @SerialName("in_progress")
    IN_PROGRESS,

    @SerialName("requires_action")
    REQUIRES_ACTION,

    @SerialName("cancelling")
    CANCELLING,

    @SerialName("cancelled")
    CANCELLED,

    @SerialName("failed")
    FAILED,

    @SerialName("completed")
    COMPLETED,

    @SerialName("expired")
    EXPIRED
}