/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.api.commanding.commands

import com.fluxtah.ask.api.clients.openai.assistants.AssistantsApi
import com.fluxtah.ask.api.clients.openai.assistants.model.AssistantThread
import com.fluxtah.ask.api.printers.AskResponsePrinter
import com.fluxtah.ask.api.store.user.UserProperties
import kotlinx.serialization.encodeToString

class GetThread(
    private val assistantsApi: AssistantsApi,
    private val userProperties: UserProperties,
    private val printer: AskResponsePrinter
) : Command() {
    override val requiresApiKey: Boolean = true
    override suspend fun execute(args: List<String>) {
        val threadId = if (args.isEmpty()) null else args.first()

        val actualThread = threadId ?: userProperties.getThreadId().ifEmpty { null }

        if (actualThread == null) {
            printer.printMessage("You need to create a thread first. Use /thread-new or pass a thread id as the first argument")
            return
        }
        println(JSON.encodeToString<AssistantThread>(assistantsApi.threads.getThread(actualThread)))
    }
}

