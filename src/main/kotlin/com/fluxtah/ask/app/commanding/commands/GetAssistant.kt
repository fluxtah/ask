/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.app.commanding.commands

import com.fluxtah.ask.api.clients.openai.assistants.AssistantsApi
import com.fluxtah.ask.api.clients.openai.assistants.model.Assistant
import kotlinx.serialization.encodeToString

class GetAssistant(private val assistantsApi: AssistantsApi, private val assistantId: String) :
    Command() {
    override val requiresApiKey: Boolean = true
    override suspend fun execute() {
        println(JSON.encodeToString<Assistant>(assistantsApi.assistants.getAssistant(assistantId)))
    }
}