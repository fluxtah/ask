/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.app.commanding.commands

import com.fluxtah.ask.api.assistants.AssistantInstallRepository
import com.fluxtah.ask.api.assistants.AssistantRegistry
import com.fluxtah.ask.api.printers.AskResponsePrinter

class UnInstallAssistant(
    private val assistantRegistry: AssistantRegistry,
    private val assistantInstallRepository: AssistantInstallRepository,
    private val printer: AskResponsePrinter,
    private val assistantId: String
) : Command() {
    override val requiresApiKey: Boolean = true
    override suspend fun execute() {
        val def = assistantRegistry.getAssistantById(assistantId)

        if (def == null) {
            printer.println("Assistant not found: @$assistantId")
            return
        }

        val assistantInstallRecord = assistantInstallRepository.getAssistantInstallRecord(assistantId)

        if (assistantInstallRecord == null) {
            printer.println("Assistant @${def.id} ${def.version} not installed.")
            return
        }

        if (assistantInstallRepository.uninstall(assistantInstallRecord)) {
            printer.println("Uninstalled assistant: @${def.id} ${assistantInstallRecord.version} ${assistantInstallRecord.installId}")
        } else {
            printer.println("Failed to uninstall assistant: @${def.id} ${assistantInstallRecord.version} ${assistantInstallRecord.installId}")
        }
    }
}
