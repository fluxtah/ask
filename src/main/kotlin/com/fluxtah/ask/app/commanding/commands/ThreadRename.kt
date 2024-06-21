/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.app.commanding.commands

import com.fluxtah.ask.api.printers.AskResponsePrinter
import com.fluxtah.ask.api.repository.ThreadRepository

class ThreadRename(
    private val threadRepository: ThreadRepository,
    private val responsePrinter: AskResponsePrinter
) : Command() {
    override val requiresApiKey: Boolean = false
    override suspend fun execute(args: List<String>) {
        if (args.size != 2) {
            responsePrinter.println("Invalid number of arguments for /thread-rename, expected a thread ID and new title following the command")
            return
        }

        val threadId = args[0]
        val newTitle = args[1]
        threadRepository.renameThread(threadId, newTitle)
    }
}