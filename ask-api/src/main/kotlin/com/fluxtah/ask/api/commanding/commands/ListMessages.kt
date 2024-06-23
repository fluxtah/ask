/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.api.commanding.commands

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
    override suspend fun execute(args: List<String>) {
        val threadId = userProperties.getThreadId()
        if (threadId.isEmpty()) {
            printer.printMessage("You need to create a thread first. Use /thread-new")
            return
        }
        printer
            .begin()
            .println()
            .println(String.format("%-19s %-28s %-10s %-28s", "Date", "ID", "Role", "Content"))
            .println("-----------------------------------------------------------------------------------------------")
            .apply {
                assistantsApi.messages.listMessages(threadId).data.forEach {
                    val contentShortened = it.content.joinToString { it.text.value }.lines().first().take(32)
                    val contentElipsised =
                        if (contentShortened.length < 32) contentShortened else "$contentShortened..."
                    println(
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
            .end()
    }
}

