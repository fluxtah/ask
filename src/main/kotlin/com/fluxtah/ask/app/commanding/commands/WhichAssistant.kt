/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.app.commanding.commands

import com.fluxtah.ask.api.assistants.AssistantInstallRepository
import com.fluxtah.ask.api.assistants.AssistantRegistry
import com.fluxtah.ask.app.UserProperties

class WhichAssistant(
    private val userProperties: UserProperties,
    private val assistantRegistry: AssistantRegistry,
    private val assistantInstallRepository: AssistantInstallRepository
) : Command() {
    override val requiresApiKey: Boolean = false
    override suspend fun execute() {
        val assistantId = userProperties.getAssistantId()
        if (assistantId.isEmpty()) {
            println("You need to select an assistant first. Use /assistant-list to see available assistants")
            return
        }

        assistantRegistry.getAssistantById(assistantId)?.let {
            val installedAssistants = assistantInstallRepository.getAssistantInstallRecords()
            val installed = installedAssistants.find { record -> record.id == it.id } != null
            println("@${it.id} - ${it.name} ${it.version}, ${it.model}, installed: $installed")
        } ?: println("Assistant not found")
    }
}
