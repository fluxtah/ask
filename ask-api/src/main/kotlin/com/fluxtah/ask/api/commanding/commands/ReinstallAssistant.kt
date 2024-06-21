package com.fluxtah.ask.api.commanding.commands

import com.fluxtah.ask.api.assistants.AssistantInstallRepository
import com.fluxtah.ask.api.assistants.AssistantRegistry
import com.fluxtah.ask.api.printers.AskResponsePrinter

class ReinstallAssistant(
    private val assistantRegistry: AssistantRegistry,
    private val assistantInstallRepository: AssistantInstallRepository,
    private val printer: AskResponsePrinter,
) : Command() {
    override val requiresApiKey: Boolean = true
    override suspend fun execute(args: List<String>) {
        if (args.size != 1) {
            printer.println("Invalid number of arguments for /assistant-reinstall, expected an assistant ID following the command")
            return
        }

        val assistantId = args[0]

        val def = assistantRegistry.getAssistantById(assistantId)

        if (def == null) {
            printer.println("Assistant not found: $assistantId")
            return
        }

        val assistantInstallRecord = assistantInstallRepository.getAssistantInstallRecord(assistantId)

        if (assistantInstallRecord != null) {
            if (assistantInstallRepository.uninstall(assistantInstallRecord)) {
                printer.println("Uninstalled assistant: @${def.id} ${assistantInstallRecord.version} ${assistantInstallRecord.installId}")
            } else {
                printer.println("Failed to uninstall assistant: @${def.id} ${assistantInstallRecord.version} ${assistantInstallRecord.installId}")
                return
            }
        }

        val newAssistantInstallRecord = assistantInstallRepository.install(def)
        printer.println("Installed assistant: @${def.id} ${def.version} ${newAssistantInstallRecord.installId}")
    }
}
