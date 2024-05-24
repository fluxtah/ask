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
        println()
        println(String.format("%-10s %-10s %-16s %-12s %-8s", "ID", "Version", "Name", "Installed", "Upgrade"))
        println("-----------------------------------------------------------------")
        assistantRegistry.getAssistants().forEach {
            val installedAssistant = installedAssistants.find { record -> record.id == it.id }
            val currentVersion = installedAssistant?.version ?: it.version
            val upgradeAvailable =
                if (installedAssistant != null && installedAssistant.version != it.version) "✔ (v${it.version})" else "x"
            println(
                String.format(
                    "%-10s %-10s %-16s %-12s %-8s",
                    it.id,
                    currentVersion,
                    it.name,
                    if(installedAssistant != null) "✔" else "x",
                    upgradeAvailable
                )
            )
           // println("@${it.id} - ${it.name} $currentVersion, ${it.model}, installed: ${installedAssistant != null}$upgradeAvailable")
        }
    }
}