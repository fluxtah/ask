/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.app.commanding.commands

import com.fluxtah.ask.api.clients.openai.assistants.AssistantsApi
import com.fluxtah.ask.api.clients.openai.assistants.model.AssistantRunStepList
import com.fluxtah.ask.app.UserProperties
import kotlinx.serialization.encodeToString

class ListRunSteps(private val assistantsApi: AssistantsApi, private val userProperties: UserProperties) :
    Command() {
    override val requiresApiKey: Boolean = true
    override suspend fun execute() {
        val threadId = userProperties.getThreadId()
        if (threadId.isEmpty()) {
            println("You need to create a thread first. Use /thread-new")
            return
        }
        val runId = userProperties.getRunId()
        if (runId.isEmpty()) {
            println("You need to create a run first. Use /run-new")
            return
        }

        println(JSON.encodeToString<AssistantRunStepList>(assistantsApi.runs.listRunSteps(threadId, runId)))
    }
}