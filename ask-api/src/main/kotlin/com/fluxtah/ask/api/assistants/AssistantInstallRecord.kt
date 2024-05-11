/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.api.assistants

import kotlinx.serialization.Serializable

@Serializable
data class AssistantInstallRecord(
    val id: String,
    val version: String,
    val installId: String
)