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
            val installedAssistant = installedAssistants.find { record -> record.id == it.id }
            val currentVersion = installedAssistant?.version ?: it.version
            val upgradeAvailable =
                if (installedAssistant != null && installedAssistant.version != it.version) " (upgrade available v${it.version})" else ""
            println("@${it.id} - ${it.name} $currentVersion, ${it.model}, installed: ${installedAssistant != null}$upgradeAvailable")
        }
    }
}