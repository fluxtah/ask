/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.app.commanding.commands

import com.fluxtah.ask.api.clients.openai.assistants.AssistantsApi

class ListThreads(private val assistantsApi: AssistantsApi) : Command() {
    override val requiresApiKey: Boolean = true
    override suspend fun execute() {
        // TODO currently its not possible to list threads though the API should be up soon
        println(assistantsApi.threads.listThreads())
    }
}