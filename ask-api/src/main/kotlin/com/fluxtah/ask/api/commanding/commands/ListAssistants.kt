/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.api.commanding.commands

import com.fluxtah.ask.api.ansi.cyan
import com.fluxtah.ask.api.assistants.AssistantInstallRepository
import com.fluxtah.ask.api.assistants.AssistantRegistry
import com.fluxtah.ask.api.printers.AskResponsePrinter
import com.fluxtah.ask.api.version.VersionUtils

class ListAssistants(
    private val assistantRegistry: AssistantRegistry,
    private val assistantInstallRepository: AssistantInstallRepository,
    private val printer: AskResponsePrinter
) : Command() {
    override val requiresApiKey: Boolean = true
    override suspend fun execute(args: List<String>) {
        val installedAssistants = assistantInstallRepository.getAssistantInstallRecords()
        printer.println()
        printer.println(String.format("%-10s %-10s %-16s %-12s %-8s", "ID", "Version", "Name", "Installed", "Update"))
        printer.println("-----------------------------------------------------------------")
        assistantRegistry.getAssistants().forEach {
            val installedAssistant = installedAssistants.find { record -> record.id == it.id }
            val currentVersion = installedAssistant?.version ?: it.version
            val upgradeAvailable =
                if (installedAssistant != null &&
                    VersionUtils.isVersionGreater(it.version, installedAssistant.version)
                ) {
                    cyan(it.version)
                } else {
                    "x"
                }
            printer.println(
                String.format(
                    "%-10s %-10s %-16s %-12s %-8s",
                    it.id,
                    currentVersion,
                    it.name,
                    if (installedAssistant != null) "✔" else "x",
                    upgradeAvailable
                )
            )
        }
    }
}