/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.api.commanding.commands

import com.fluxtah.ask.api.printers.AskResponsePrinter
import com.fluxtah.ask.api.repository.ThreadRepository
import com.fluxtah.ask.api.store.user.UserProperties

class SwitchThread(
    private val userProperties: UserProperties,
    private val threadRepository: ThreadRepository,
    private val printer: AskResponsePrinter
) : Command() {
    override val requiresApiKey: Boolean = true
    override suspend fun execute(args: List<String>) {
        if (args.size != 1) {
            printer.println("Invalid number of arguments for /thread-switch, expected a thread ID following the command")
            return
        }

        val threadId = args.first()
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
