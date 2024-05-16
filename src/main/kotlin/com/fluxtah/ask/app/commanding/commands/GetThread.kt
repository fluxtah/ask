/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.app.commanding.commands

import com.fluxtah.ask.api.clients.openai.assistants.AssistantsApi
import com.fluxtah.ask.api.clients.openai.assistants.model.AssistantThread
import com.fluxtah.ask.api.UserProperties
import kotlinx.serialization.encodeToString

class GetThread(
    private val assistantsApi: AssistantsApi,
    private val userProperties: UserProperties,
    private val threadId: String? = null
) : Command() {
    override val requiresApiKey: Boolean = true
    override suspend fun execute() {
        val actualThread = threadId ?: userProperties.getThreadId().ifEmpty { null }

        if (actualThread == null) {
            println("You need to create a thread first. Use /thread-new or pass a thread as the first argument")
            return
        }
        println(JSON.encodeToString<AssistantThread>(assistantsApi.threads.getThread(actualThread)))
    }
}

