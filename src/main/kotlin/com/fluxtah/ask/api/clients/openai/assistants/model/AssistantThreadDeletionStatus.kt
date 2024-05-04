/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.api.clients.openai.assistants.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AssistantThreadDeletionStatus(
    @SerialName("id") val id: String,
    @SerialName("object") val objectName: String,
    @SerialName("deleted") val deleted: Boolean
)