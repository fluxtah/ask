/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.api.commanding.commands

import com.fluxtah.ask.api.clients.openai.assistants.AssistantsApi
import com.fluxtah.ask.api.clients.openai.assistants.model.AssistantRunStepList
import com.fluxtah.ask.api.printers.AskResponsePrinter
import com.fluxtah.ask.api.store.user.UserProperties
import kotlinx.serialization.encodeToString

class ListRunSteps(
    private val assistantsApi: AssistantsApi,
    private val userProperties: UserProperties,
    private val printer: AskResponsePrinter
) :
    Command() {
    override val requiresApiKey: Boolean = true
    override suspend fun execute(args: List<String>) {
        val threadId = userProperties.getThreadId()
        if (threadId.isEmpty()) {
            printer.println("You need to create a thread first. Use /thread-new")
            return
        }
        val runId = userProperties.getRunId()
        if (runId.isEmpty()) {
            printer.println("No last run")
            return
        }

        printer.println(JSON.encodeToString<AssistantRunStepList>(assistantsApi.runs.listRunSteps(threadId, runId)))
    }
}