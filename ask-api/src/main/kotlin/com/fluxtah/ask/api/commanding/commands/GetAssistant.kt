/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.api.commanding.commands

import com.fluxtah.ask.api.assistants.AssistantInstallRepository
import com.fluxtah.ask.api.assistants.AssistantRegistry
import com.fluxtah.ask.api.printers.AskResponsePrinter

class GetAssistant(
    private val assistantRegistry: AssistantRegistry,
    private val assistantInstallRepository: AssistantInstallRepository,
    private val responsePrinter: AskResponsePrinter,
) :
    Command() {
    override val requiresApiKey: Boolean = true
    override suspend fun execute(args: List<String>) {
        if (args.size != 1) {
            responsePrinter.println("Invalid number of arguments for /assistant-info, expected a assistant ID following the command")
            return
        }

        val assistantId = args.first()

        val assistantDef = assistantRegistry.getAssistantById(assistantId)
        if (assistantDef == null) {
            responsePrinter.println("Assistant not found")
            return
        }

        val assistantInstallRecord = assistantInstallRepository.getAssistantInstallRecord(assistantId)

        val installed = assistantInstallRecord != null

        responsePrinter.println("@${assistantDef.id} - ${assistantDef.name} ${assistantDef.version}, ${assistantDef.model}, installed: $installed")
    }
}
