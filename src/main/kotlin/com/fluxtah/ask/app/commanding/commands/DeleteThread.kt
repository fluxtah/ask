package com.fluxtah.ask.app.commanding.commands

import com.fluxtah.ask.api.clients.openai.assistants.AssistantsApi
import com.fluxtah.ask.api.repository.ThreadRepository

class DeleteThread(
    private val assistantsApi: AssistantsApi,
    private val threadRepository: ThreadRepository,
    private val threadId: String
) : Command() {
    override val requiresApiKey: Boolean = true
    override suspend fun execute() {
        assistantsApi.threads.deleteThread(threadId)
        threadRepository.deleteThread(threadId)
        println("Thread deleted")
    }
}