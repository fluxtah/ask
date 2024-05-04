/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.api.clients.openai.assistants.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ToolResources(
    @SerialName("code_interpreter") val codeInterpreter: ToolResourcesCodeInterpreter? = null,
    @SerialName("file_search") val fileSearch: ToolResourcesFileSearch? = null,
)