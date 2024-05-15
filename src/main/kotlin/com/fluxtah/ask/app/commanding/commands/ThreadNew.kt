/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.app.commanding.commands

import com.fluxtah.ask.api.clients.openai.assistants.AssistantsApi
import com.fluxtah.ask.api.UserProperties
import com.fluxtah.ask.api.repository.ThreadRepository
import java.util.*

class ThreadNew(
    private val assistantsApi: AssistantsApi,
    private val userProperties: UserProperties,
    private val threadRepository: ThreadRepository
) :
    Command() {
    override val requiresApiKey: Boolean = true
    override suspend fun execute() {
        val thread = assistantsApi.threads.createThread()
        println("Created thread: ${thread.id} at ${Date(thread.createdAt)}")
        userProperties.setThreadId(thread.id)
        userProperties.save()
        threadRepository.createThread(thread.id, "")
    }
}
