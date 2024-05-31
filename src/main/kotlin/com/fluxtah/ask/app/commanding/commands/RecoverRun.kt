/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.app.commanding.commands

import com.fluxtah.ask.api.AssistantRunManager

class RecoverRun(private val assistantRunManager: AssistantRunManager) : Command() {
    override val requiresApiKey: Boolean = true
    override suspend fun execute() {
        assistantRunManager.recoverRun()
    }
}
