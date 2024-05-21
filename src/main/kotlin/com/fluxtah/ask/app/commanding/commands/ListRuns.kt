/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.app.commanding.commands

import com.fluxtah.ask.api.clients.openai.assistants.AssistantsApi
import com.fluxtah.ask.app.UserProperties
import java.util.*

class ListRuns(private val assistantsApi: AssistantsApi, private val userProperties: UserProperties) : Command() {
    override val requiresApiKey: Boolean = true
    override suspend fun execute() {
        val threadId = userProperties.getThreadId()
        if (threadId.isEmpty()) {
            println("You need to create a thread first. Use /thread-new")
            return
        }
        println()
        println(String.format("%-19s %-28s %-12s %-10s %-10s", "Created", "ID", "Status", "In", "Out"))
        println("------------------------------------------------------------------------------------")
        assistantsApi.runs.listRuns(threadId).data.forEach {
            println(String.format("%-19s %-28s %-12s %-10s %-10s", it.createdAt.toShortDateTimeString(), it.id, it.status, it.usage?.promptTokens, it.usage?.completionTokens))
        }
    }
}

