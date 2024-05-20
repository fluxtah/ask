/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.app.commanding.commands

import com.fluxtah.ask.api.clients.openai.assistants.AssistantsApi
import com.fluxtah.ask.api.clients.openai.assistants.model.AssistantMessageList
import com.fluxtah.ask.app.UserProperties
import kotlinx.serialization.encodeToString

class ListMessages(private val assistantsApi: AssistantsApi, private val userProperties: UserProperties) :
    Command() {
    override val requiresApiKey: Boolean = true
    override suspend fun execute() {
        val threadId = userProperties.getThreadId()
        if (threadId.isEmpty()) {
            println("You need to create a thread first. Use /thread-new")
            return
        }
        println()
        println(String.format("%-28s %-10s %-28s", "ID", "Role", "Content"))
        println("--------------------------------------------------------------------------------")
        assistantsApi.messages.listMessages(threadId).data.forEach {
            val contentShortened = it.content.joinToString { it.text.value }.take(32)
            val contentElipsised = if (contentShortened.length < 32) contentShortened else "$contentShortened..."
            println(String.format("%-28s %-10s %-28s", it.id, it.role, contentElipsised))
        }
    }
}

