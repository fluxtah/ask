package com.fluxtah.ask.app.commanding.commands

import com.fluxtah.ask.api.repository.ThreadRepository

class ThreadRename(private val threadRepository: ThreadRepository, private val threadId: String, private val newTitle: String) : Command() {
    override val requiresApiKey: Boolean = false
    override suspend fun execute() {
        threadRepository.renameThread(threadId, newTitle)
    }
}