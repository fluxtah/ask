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
    private val printer: AskResponsePrinter
) : Command() {
    override val requiresApiKey: Boolean = true
    override suspend fun execute(args: List<String>) {

        if (args.size != 1) {
            printer.println("Invalid number of arguments for /assistant-uninstall, expected an assistant ID following the command")
            return
        }

        val assistantId = args.first()

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
