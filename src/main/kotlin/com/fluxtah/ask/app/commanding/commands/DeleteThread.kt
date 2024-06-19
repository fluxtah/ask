package com.fluxtah.ask.app.commanding.commands

import com.fluxtah.ask.api.clients.openai.assistants.AssistantsApi
import com.fluxtah.ask.api.printers.AskResponsePrinter
import com.fluxtah.ask.api.repository.ThreadRepository
import com.fluxtah.ask.api.store.user.UserProperties

class DeleteThread(
    private val assistantsApi: AssistantsApi,
    private val threadRepository: ThreadRepository,
    private val userProperties: UserProperties,
    private val printer: AskResponsePrinter,
    private val threadId: String,
) : Command() {
    override val requiresApiKey: Boolean = true
    override suspend fun execute() {
        assistantsApi.threads.deleteThread(threadId)
        threadRepository.deleteThread(threadId)
        if (userProperties.getThreadId() == threadId) {
            userProperties.setThreadId("")
            userProperties.save()
        }
        printer.println("Thread deleted")
    }
}