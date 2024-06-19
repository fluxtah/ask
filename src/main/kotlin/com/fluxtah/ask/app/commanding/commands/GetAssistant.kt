/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.app.commanding.commands

import com.fluxtah.ask.api.assistants.AssistantInstallRepository
import com.fluxtah.ask.api.assistants.AssistantRegistry
import com.fluxtah.ask.api.clients.openai.assistants.AssistantsApi
import com.fluxtah.ask.api.printers.AskResponsePrinter

class GetAssistant(
    private val assistantRegistry: AssistantRegistry,
    private val assistantInstallRepository: AssistantInstallRepository,
    private val responsePrinter: AskResponsePrinter,
    private val assistantId: String
) :
    Command() {
    override val requiresApiKey: Boolean = true
    override suspend fun execute() {
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
