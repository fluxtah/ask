/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.api.commanding.commands

import com.fluxtah.ask.api.printers.AskResponsePrinter
import com.fluxtah.ask.api.repository.ThreadRepository
import com.fluxtah.ask.api.store.user.UserProperties

class ListThreads(
    private val userProperties: UserProperties,
    private val threadRepository: ThreadRepository,
    private val printer: AskResponsePrinter
) : Command() {
    override val requiresApiKey: Boolean = true
    override suspend fun execute(args: List<String>) {
        val threads = threadRepository.listThreads()
        printer
            .begin()
            .println()
            .println(String.format("%-36s %-30s", "Thread", "Title"))
            .println("--------------------------------------------------------------------------------")
            .apply {
                if (threads.isEmpty()) {
                    println("No threads found, type /thread-new to create a new thread")
                } else {
                    threads.forEach {
                        val title = it.title.ifEmpty { "<unnamed>" }
                        if (userProperties.getThreadId() == it.threadId) {
                            println(String.format("%-36s %-30s", it.threadId, "$title (Active)"))
                        } else {
                            println(String.format("%-36s %-30s", it.threadId, title))
                        }
                    }
                }
            }
            .end()
    }
}