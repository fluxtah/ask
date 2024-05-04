/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.app.commanding.commands

import com.fluxtah.ask.api.assistants.AssistantInstallRepository
import com.fluxtah.ask.api.assistants.AssistantRegistry

class ListAssistants(
    private val assistantRegistry: AssistantRegistry,
    private val assistantInstallRepository: AssistantInstallRepository
) : Command() {
    override val requiresApiKey: Boolean = true
    override suspend fun execute() {
        val installedAssistants = assistantInstallRepository.getAssistantInstallRecords()
        assistantRegistry.getAssistants().forEach {
            println("@${it.id} - ${it.name} ${it.version}, ${it.model}, installed: ${installedAssistants.find { record -> record.id == it.id } != null}")
        }
    }
}