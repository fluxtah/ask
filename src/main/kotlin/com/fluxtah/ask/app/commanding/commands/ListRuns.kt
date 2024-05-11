/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.app.commanding.commands

import com.fluxtah.ask.api.clients.openai.assistants.AssistantsApi
import com.fluxtah.ask.api.UserProperties
import java.util.*

class ListRuns(private val assistantsApi: AssistantsApi, private val userProperties: UserProperties) : Command() {
    override val requiresApiKey: Boolean = true
    override suspend fun execute() {
        val threadId = userProperties.getThreadId()
        if (threadId.isEmpty()) {
            println("You need to create a thread first. Use /thread-new")
            return
        }
        assistantsApi.runs.listRuns(threadId).data.forEach {
            println("${it.id}, created: ${Date(it.createdAt)}, status: ${it.status}, last error: ${it.lastError}")
        }
    }
}