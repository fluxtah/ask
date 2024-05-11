/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.app.commanding.commands

import com.fluxtah.ask.api.UserProperties

class WhichThread(private val userProperties: UserProperties) : Command() {
    override val requiresApiKey: Boolean = false
    override suspend fun execute() {
        println("Current thread: ${userProperties.getThreadId().ifEmpty { "None" }}")
    }
}