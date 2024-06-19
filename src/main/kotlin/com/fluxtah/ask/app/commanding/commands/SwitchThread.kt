/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.app.commanding.commands

import com.fluxtah.ask.api.printers.AskResponsePrinter
import com.fluxtah.ask.api.repository.ThreadRepository
import com.fluxtah.ask.api.store.user.UserProperties

class SwitchThread(
    private val userProperties: UserProperties,
    private val threadRepository: ThreadRepository,
    private val printer: AskResponsePrinter,
    private val threadId: String
) : Command() {
    override val requiresApiKey: Boolean = true
    override suspend fun execute() {
        val thread = threadRepository.getThreadById(threadId)
        if (thread != null) {
            userProperties.setThreadId(threadId)
            userProperties.save()
            printer.println("Switched to thread: $threadId")
        } else {
            printer.println("Thread with ID $threadId not found")
        }
    }
}
