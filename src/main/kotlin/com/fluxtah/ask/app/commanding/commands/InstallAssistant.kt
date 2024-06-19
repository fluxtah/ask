/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.app.commanding.commands

import com.fluxtah.ask.api.assistants.AssistantInstallRepository
import com.fluxtah.ask.api.assistants.AssistantRegistry
import com.fluxtah.ask.api.printers.AskResponsePrinter

class InstallAssistant(
    private val assistantRegistry: AssistantRegistry,
    private val assistantInstallRepository: AssistantInstallRepository,
    private val printer: AskResponsePrinter,
    val assistantId: String
) : Command() {
    override val requiresApiKey: Boolean = true
    override suspend fun execute() {
        val def = assistantRegistry.getAssistantById(assistantId)

        if (def == null) {
            printer.println("Assistant not found: $assistantId")
            return
        }

        val assistantInstallRecord = assistantInstallRepository.install(def)

        printer.println("Installed assistant: @${def.id} ${def.version} as ${assistantInstallRecord.installId}")
    }
}
