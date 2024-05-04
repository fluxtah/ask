/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.api.assistants

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