/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.app.commanding.commands

import com.fluxtah.ask.api.clients.openai.assistants.HTTP_LOG

data object ShowHttpLog : Command() {
    override val requiresApiKey: Boolean = false
    override suspend fun execute() {
        HTTP_LOG.forEach {
            println(it)
        }
    }
}