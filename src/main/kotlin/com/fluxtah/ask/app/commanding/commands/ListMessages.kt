/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.app.commanding.commands

import com.fluxtah.ask.api.clients.openai.assistants.AssistantsApi
import com.fluxtah.ask.api.printers.AskResponsePrinter
import com.fluxtah.ask.api.store.user.UserProperties

class ListMessages(
    private val assistantsApi: AssistantsApi,
    private val userProperties: UserProperties,
    private val printer: AskResponsePrinter
) :
    Command() {
    override val requiresApiKey: Boolean = true
    override suspend fun execute() {
        val threadId = userProperties.getThreadId()
        if (threadId.isEmpty()) {
            println("You need to create a thread first. Use /thread-new")
            return
        }
        printer.println()
        printer.println(String.format("%-19s %-28s %-10s %-28s", "Date", "ID", "Role", "Content"))
        printer.println("-----------------------------------------------------------------------------------------------")
        assistantsApi.messages.listMessages(threadId).data.forEach {
            val contentShortened = it.content.joinToString { it.text.value }.lines().first().take(32)
            val contentElipsised = if (contentShortened.length < 32) contentShortened else "$contentShortened..."
            printer.println(
                String.format(
                    "%-19s %-28s %-10s %-28s",
                    it.createdAt.toShortDateTimeString(),
                    it.id,
                    it.role,
                    contentElipsised
                )
            )
        }
    }
}

