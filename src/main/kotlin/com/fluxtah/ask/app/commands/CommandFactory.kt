/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.app.commands

import com.fluxtah.ask.api.assistants.AssistantInstallRepository
import com.fluxtah.ask.api.assistants.AssistantRegistry
import com.fluxtah.ask.api.clients.openai.assistants.AssistantsApi
import com.fluxtah.ask.app.UserProperties

class CommandFactory(
    private val assistantsApi: AssistantsApi,
    private val assistantRegistry: AssistantRegistry,
    private val assistantInstallRepository: AssistantInstallRepository,
    private val userProperties: UserProperties
) {
    private val commands = mapOf<String, (List<String>) -> Command>(
        "/help" to { Command.Help },
        "/exit" to { Command.Exit },
        "/thread-new" to { Command.CreateAssistantThread(assistantsApi, userProperties) },
        "/thread-which" to { Command.WhichThread(userProperties) },
        "/thread-info" to {
            if (it.isEmpty()) {
                Command.GetThread(assistantsApi, userProperties, null)
            } else {
                Command.GetThread(assistantsApi, userProperties, it.first())
            }
        },
        "/thread-list" to { Command.ListThreads(assistantsApi) },
        "/message-list" to { Command.ListMessages(assistantsApi, userProperties) },
        "/run-list" to { Command.ListRuns(assistantsApi, userProperties) },
        "/run-step-list" to { Command.ListRunSteps(assistantsApi, userProperties) },
        "/http-log" to { Command.ShowHttpLog },
        "/assistant-install" to {
            if (it.size != 1) {
                Command.UnknownCommand("Invalid number of arguments for /assistant-install, expected an assistant ID following the command")
            } else {
                Command.InstallAssistant(assistantRegistry, assistantInstallRepository, it.first())
            }
        },
        "/assistant-list" to {
            Command.ListAssistants(assistantRegistry, assistantInstallRepository)
        },
        "/assistant-info" to {
            if (it.size != 1) {
                Command.UnknownCommand("Invalid number of arguments for /assistant-info, expected a assistant ID following the command")
            } else {
                Command.GetAssistant(assistantsApi, it.first())
            }
        },
        "/set-key" to {
            if (it.size != 1) {
                Command.UnknownCommand("Invalid number of arguments for /set-key, expected an API key following the command")
            } else {
                Command.SetOpenAiApiKey(userProperties, it.first())
            }
        }
    )

    fun create(input: String): Command {
        val parts = input.split(" ")
        return commands[parts[0]]?.invoke(parts.drop(1)) ?: Command.UnknownCommand("Unknown command: ${parts[0]}")
    }
}
