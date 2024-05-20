/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.app.commanding.commands

import com.fluxtah.ask.api.clients.openai.assistants.AssistantsApi
import com.fluxtah.ask.app.UserProperties
import com.fluxtah.ask.api.repository.ThreadRepository

class SwitchThread(
    private val assistantsApi: AssistantsApi,
    private val userProperties: UserProperties,
    private val threadRepository: ThreadRepository,
    private val threadId: String
) : Command() {
    override val requiresApiKey: Boolean = true
    override suspend fun execute() {
        val thread = threadRepository.getThreadById(threadId)
        if (thread != null) {
            userProperties.setThreadId(threadId)
            userProperties.save()
            println("Switched to thread: $threadId")
        } else {
            println("Thread with ID $threadId not found")
        }
    }
}
