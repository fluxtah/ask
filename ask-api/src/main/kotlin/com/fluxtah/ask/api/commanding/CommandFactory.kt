/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.api.commanding

import com.fluxtah.ask.api.commanding.commands.Command
import com.fluxtah.ask.api.printers.AskResponsePrinter
import com.fluxtah.ask.api.store.user.UserProperties
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

data class CommandEntry(val name: String, val description: String, val command: () -> Command)

class CommandFactory(
    private val responsePrinter: AskResponsePrinter,
    private val userProperties: UserProperties,
) : KoinComponent {
    val commands = mutableMapOf<String, CommandEntry>()

    inline fun <reified T : Command> registerCommand(name: String, description: String) {
        commands[name] = CommandEntry(name, description) { get<T>() }
    }

    suspend fun executeCommand(input: String) {
        val parts = input.drop(1).split(" ")
        val command = commands[parts[0]]?.command?.invoke()

        if (command == null) {
            responsePrinter.begin().println("Command not found: ${parts[0]}").end()
            return
        }

        if (command.requiresApiKey) {
            if (userProperties.getOpenaiApiKey().isEmpty()) {
                responsePrinter
                    .begin().println("You need to set an OpenAI API key first! with /set-key <api-key>").end()
                return
            }
        }
        command.execute(parts.drop(1))
    }

    fun getCommandsSortedByName(): List<CommandEntry> {
        return commands.values.sortedBy { it.name }
    }
}