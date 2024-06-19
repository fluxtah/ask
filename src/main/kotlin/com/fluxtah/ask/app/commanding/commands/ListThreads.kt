/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.app.commanding.commands

import com.fluxtah.ask.api.printers.AskResponsePrinter
import com.fluxtah.ask.api.repository.ThreadRepository
import com.fluxtah.ask.api.store.user.UserProperties

class ListThreads(
    private val userProperties: UserProperties,
    private val threadRepository: ThreadRepository,
    private val printer: AskResponsePrinter
) : Command() {
    override val requiresApiKey: Boolean = true
    override suspend fun execute() {
        val threads = threadRepository.listThreads()
        printer.println()
        printer.println(String.format("%-36s %-30s", "Thread", "Title"))
        printer.println("--------------------------------------------------------------------------------")
        if (threads.isEmpty()) {
            printer.println("No threads found, type /thread-new to create a new thread")
        } else {
            threads.forEach {
                val title = it.title.ifEmpty { "<unnamed>" }
                if (userProperties.getThreadId() == it.threadId) {
                    printer.println(String.format("%-36s %-30s", it.threadId, "$title (Active)"))
                } else {
                    printer.println(String.format("%-36s %-30s", it.threadId, title))
                }
            }
        }
    }
}