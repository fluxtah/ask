package com.fluxtah.ask.api.assistants

abstract class AssistantDefinition(
    val name: String,
    val id: String,
    val version: String,
    val instructions: String,
    val functions: Any
) {
    fun install() {
        println("Installing assistant $name")
    }

    fun isInstalled(): Boolean {
        if (installId.isEmpty()) {
            return false
        }

        return true
    }

    abstract val installId: String
}

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