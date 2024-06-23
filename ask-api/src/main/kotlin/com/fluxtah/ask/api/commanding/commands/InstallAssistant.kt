/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.api.commanding.commands

import com.fluxtah.ask.api.assistants.AssistantInstallRepository
import com.fluxtah.ask.api.assistants.AssistantRegistry
import com.fluxtah.ask.api.printers.AskResponsePrinter

class InstallAssistant(
    private val assistantRegistry: AssistantRegistry,
    private val assistantInstallRepository: AssistantInstallRepository,
    private val printer: AskResponsePrinter,
) : Command() {
    override val requiresApiKey: Boolean = true
    override suspend fun execute(args: List<String>) {
        if (args.size != 1) {
            printer
                .printMessage("Invalid number of arguments for /assistant-install, expected an assistant ID following the command")
            return
        }

        val assistantId = args.first()

        val def = assistantRegistry.getAssistantById(assistantId)

        if (def == null) {
            printer.printMessage("Assistant not found: $assistantId")
            return
        }

        val assistantInstallRecord = assistantInstallRepository.install(def)

        printer
            .printMessage("Installed assistant: @${def.id} ${def.version} as ${assistantInstallRecord.installId}")
    }
}
