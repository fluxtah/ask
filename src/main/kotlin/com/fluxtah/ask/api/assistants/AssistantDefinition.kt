package com.fluxtah.ask.api.assistants

import kotlinx.serialization.Serializable

@Serializable
data class AssistantInstallRecord(
    val id: String,
    val version: String,
    val installId: String
)

abstract class AssistantDefinition(
    val id: String,
    val name: String,
    val model: String,
    val temperature: Float,
    val description: String,
    val version: String,
    val instructions: String,
    val functions: Any
)

class AssistantRegistry {
    private val assistants = mutableListOf<AssistantDefinition>()

    fun register(assistant: AssistantDefinition) {
        assistants.add(assistant)
    }

    fun getAssistantById(id: String): AssistantDefinition? {
        return assistants.find { it.id == id }
    }

    fun getAssistants(): List<AssistantDefinition> {
        return assistants
    }
}