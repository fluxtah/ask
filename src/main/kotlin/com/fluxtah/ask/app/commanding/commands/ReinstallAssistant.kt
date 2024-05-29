package com.fluxtah.ask.app.commanding.commands

import com.fluxtah.ask.api.assistants.AssistantInstallRepository
import com.fluxtah.ask.api.assistants.AssistantRegistry

class ReinstallAssistant(
    private val assistantRegistry: AssistantRegistry,
    private val assistantInstallRepository: AssistantInstallRepository,
    val assistantId: String
) : Command() {
    override val requiresApiKey: Boolean = true
    override suspend fun execute() {
        val def = assistantRegistry.getAssistantById(assistantId)

        if (def == null) {
            println("Assistant not found: $assistantId")
            return
        }

        val assistantInstallRecord = assistantInstallRepository.getAssistantInstallRecord(assistantId)

        if (assistantInstallRecord != null) {
            if (assistantInstallRepository.uninstall(assistantInstallRecord)) {
                println("Uninstalled assistant: @${def.id} ${assistantInstallRecord.version} ${assistantInstallRecord.installId}")
            } else {
                println("Failed to uninstall assistant: @${def.id} ${assistantInstallRecord.version} ${assistantInstallRecord.installId}")
                return
            }
        }

        val newAssistantInstallRecord = assistantInstallRepository.install(def)
        println("Installed assistant: @${def.id} ${def.version} ${newAssistantInstallRecord.installId}")
    }
}
