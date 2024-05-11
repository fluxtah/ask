/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.api.clients.openai.assistants.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AssistantRunStepList(
    @SerialName("object") val objectName: String,
    @SerialName("data") val data: List<AssistantRunStep>,
    @SerialName("first_id") val firstId: String,
    @SerialName("last_id") val lastId: String,
    @SerialName("has_more") val hasMore: Boolean
)