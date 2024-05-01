package com.fluxtah.ask.api.assistants

import com.fluxtah.ask.api.FunctionToolGenerator
import com.fluxtah.ask.api.clients.openai.assistants.AssistantsApi
import com.fluxtah.ask.api.clients.openai.assistants.model.CreateAssistantRequest
import com.fluxtah.ask.api.clients.openai.assistants.model.ModifyAssistantRequest
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class AssistantInstallRepository(private val assistantsApi: AssistantsApi) {
    suspend fun install(assistantDef: AssistantDefinition): AssistantInstallRecord {
        val assistantInstallRecord = getAssistantInstallRecord(assistantDef.id)

        val newRecord = if (assistantInstallRecord == null) {
            createAssistantFromDef(assistantDef)
        } else {
            modifyAssistantFromDef(assistantDef, assistantInstallRecord)
        }

        saveOrReplaceAssistantInstallRecord(newRecord)

        return newRecord
    }

    private suspend fun modifyAssistantFromDef(
        assistantDef: AssistantDefinition,
        assistantInstallRecord: AssistantInstallRecord
    ): AssistantInstallRecord {
        val modifyAssistantRequest = ModifyAssistantRequest(
            model = assistantDef.model,
            name = assistantDef.name,
            description = assistantDef.description,
            instructions = assistantDef.instructions,
            tools = FunctionToolGenerator().generateToolsForInstance(assistantDef.functions),
            metadata = mapOf(
                "version" to assistantDef.version,
                "assistantId" to assistantDef.id
            ),
            temperature = assistantDef.temperature
        )

        val assistant =
            assistantsApi.assistants.modifyAssistant(assistantInstallRecord.installId, modifyAssistantRequest)

        return AssistantInstallRecord(
            id = assistantDef.id,
            version = assistantDef.version,
            installId = assistant.id
        )
    }

    private suspend fun createAssistantFromDef(assistantDef: AssistantDefinition): AssistantInstallRecord {
        val createAssistantRequest = CreateAssistantRequest(
            model = assistantDef.model,
            temperature = assistantDef.temperature,
            name = assistantDef.name,
            description = assistantDef.description,
            instructions = assistantDef.instructions,
            tools = FunctionToolGenerator().generateToolsForInstance(assistantDef.functions),
            metadata = mapOf(
                "version" to assistantDef.version,
                "assistantId" to assistantDef.id
            )
        )

        val assistant = assistantsApi.assistants.createAssistant(createAssistantRequest)

        return AssistantInstallRecord(
            id = assistantDef.id,
            version = assistantDef.version,
            installId = assistant.id
        )
    }

    fun uninstall(assistantId: String) {
        TODO()
    }

    fun getAssistantInstallRecord(assistantId: String): AssistantInstallRecord? {
        return getAssistantInstallRecords().find { it.id == assistantId }
    }

    fun getAssistantInstallRecords(): List<AssistantInstallRecord> {
        // Load from file JSONL
        val records = mutableListOf<AssistantInstallRecord>()
        val file = File("assistants.jsonl")
        if (!file.exists()) {
            return emptyList()
        }
        file.forEachLine { line ->
            val record = Json.decodeFromString<AssistantInstallRecord>(line)
            records.add(record)
        }

        return records
    }

    private fun saveOrReplaceAssistantInstallRecord(record: AssistantInstallRecord) {
        val records = getAssistantInstallRecords().toMutableList()
        val existingRecord = records.find { it.id == record.id }
        if (existingRecord != null) {
            records.remove(existingRecord)
        }
        records.add(record)

        val file = File("assistants.jsonl")
        file.writeText(records.joinToString("\n") { Json.encodeToString(it) })
    }
}
