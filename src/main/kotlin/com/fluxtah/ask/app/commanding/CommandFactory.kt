/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.app.commanding

import com.fluxtah.ask.api.assistants.AssistantInstallRepository
import com.fluxtah.ask.api.assistants.AssistantRegistry
import com.fluxtah.ask.api.clients.openai.assistants.AssistantsApi
import com.fluxtah.ask.app.UserProperties
import com.fluxtah.ask.app.commanding.commands.ClearModel
import com.fluxtah.ask.app.commanding.commands.Command
import com.fluxtah.ask.app.commanding.commands.CreateAssistantThread
import com.fluxtah.ask.app.commanding.commands.Exit
import com.fluxtah.ask.app.commanding.commands.GetAssistant
import com.fluxtah.ask.app.commanding.commands.GetThread
import com.fluxtah.ask.app.commanding.commands.Help
import com.fluxtah.ask.app.commanding.commands.InstallAssistant
import com.fluxtah.ask.app.commanding.commands.ListAssistants
import com.fluxtah.ask.app.commanding.commands.ListMessages
import com.fluxtah.ask.app.commanding.commands.ListRunSteps
import com.fluxtah.ask.app.commanding.commands.ListRuns
import com.fluxtah.ask.app.commanding.commands.ListThreads
import com.fluxtah.ask.app.commanding.commands.SetModel
import com.fluxtah.ask.app.commanding.commands.SetOpenAiApiKey
import com.fluxtah.ask.app.commanding.commands.ShowHttpLog
import com.fluxtah.ask.app.commanding.commands.UnInstallAssistant
import com.fluxtah.ask.app.commanding.commands.UnknownCommand
import com.fluxtah.ask.app.commanding.commands.WhichAssistant
import com.fluxtah.ask.app.commanding.commands.WhichModel
import com.fluxtah.ask.app.commanding.commands.WhichThread

class CommandFactory(
    private val assistantsApi: AssistantsApi,
    private val assistantRegistry: AssistantRegistry,
    private val assistantInstallRepository: AssistantInstallRepository,
    private val userProperties: UserProperties
) {
    private val commands = mapOf<String, (List<String>) -> Command>(
        "/help" to { Help },
        "/exit" to { Exit },
        "/assistant-install" to {
            if (it.size != 1) {
                UnknownCommand("Invalid number of arguments for /assistant-install, expected an assistant ID following the command")
            } else {
                InstallAssistant(assistantRegistry, assistantInstallRepository, it.first())
            }
        },
        "/assistant-uninstall" to {
            if (it.size != 1) {
                UnknownCommand("Invalid number of arguments for /assistant-uninstall, expected an assistant ID following the command")
            } else {
                UnInstallAssistant(assistantRegistry, assistantInstallRepository, it.first())
            }
        },
        "/assistant-list" to {
            ListAssistants(assistantRegistry, assistantInstallRepository)
        },
        "/assistant-which" to {
            WhichAssistant(userProperties, assistantRegistry, assistantInstallRepository)
        },
        "/assistant-info" to {
            if (it.size != 1) {
                UnknownCommand("Invalid number of arguments for /assistant-info, expected a assistant ID following the command")
            } else {
                GetAssistant(assistantRegistry, assistantInstallRepository, assistantsApi, it.first())
            }
        },
        "/model" to {
            if (it.size != 1) {
                UnknownCommand("Invalid number of arguments for /model, expected a model ID following the command")
            } else {
                SetModel(userProperties, it.first())
            }
        },
        "/model-clear" to { ClearModel(userProperties) },
        "/model-which" to { WhichModel(userProperties) },
        "/thread-new" to { CreateAssistantThread(assistantsApi, userProperties) },
        "/thread-which" to { WhichThread(userProperties) },
        "/thread-info" to {
            if (it.isEmpty()) {
                GetThread(assistantsApi, userProperties, null)
            } else {
                GetThread(assistantsApi, userProperties, it.first())
            }
        },
        "/thread-list" to { ListThreads(assistantsApi) },
        "/message-list" to { ListMessages(assistantsApi, userProperties) },
        "/run-list" to { ListRuns(assistantsApi, userProperties) },
        "/run-step-list" to { ListRunSteps(assistantsApi, userProperties) },
        "/http-log" to { ShowHttpLog },
        "/set-key" to {
            if (it.size != 1) {
                UnknownCommand("Invalid number of arguments for /set-key, expected an API key following the command")
            } else {
                SetOpenAiApiKey(userProperties, it.first())
            }
        }
    )

    fun create(input: String): Command {
        val parts = input.split(" ")
        return commands[parts[0]]?.invoke(parts.drop(1)) ?: UnknownCommand("Unknown command: ${parts[0]}")
    }
}
