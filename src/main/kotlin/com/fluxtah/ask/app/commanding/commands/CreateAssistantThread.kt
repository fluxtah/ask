/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.app.commanding.commands

import com.fluxtah.ask.api.clients.openai.assistants.AssistantsApi
import com.fluxtah.ask.api.UserProperties
import java.util.*

class CreateAssistantThread(private val assistantsApi: AssistantsApi, private val userProperties: UserProperties) :
    Command() {
    override val requiresApiKey: Boolean = true
    override suspend fun execute() {
        val thread = assistantsApi.threads.createThread()
        println("Created thread: ${thread.id} at ${Date(thread.createdAt)}")
        userProperties.setThreadId(thread.id)
        userProperties.save()
    }
}
