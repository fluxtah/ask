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
import com.fluxtah.ask.api.printers.AskResponsePrinter
import com.fluxtah.ask.app.commanding.commands.Clear
import com.fluxtah.ask.app.commanding.commands.ClearModel
import com.fluxtah.ask.app.commanding.commands.Command
import com.fluxtah.ask.app.commanding.commands.ThreadNew
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
import com.fluxtah.ask.app.commanding.commands.SetLogLevel
import com.fluxtah.ask.app.commanding.commands.SetModel
import com.fluxtah.ask.app.commanding.commands.SetOpenAiApiKey
import com.fluxtah.ask.app.commanding.commands.ShellExec
import com.fluxtah.ask.app.commanding.commands.ShowHttpLog
import com.fluxtah.ask.app.commanding.commands.ThreadRecall
import com.fluxtah.ask.app.commanding.commands.ThreadRename
import com.fluxtah.ask.app.commanding.commands.UnInstallAssistant
import com.fluxtah.ask.app.commanding.commands.UnknownCommand
import com.fluxtah.ask.app.commanding.commands.WhichAssistant
import com.fluxtah.ask.app.commanding.commands.WhichModel
import com.fluxtah.ask.app.commanding.commands.WhichThread
import com.fluxtah.ask.api.repository.ThreadRepository
import com.fluxtah.ask.app.commanding.commands.DeleteThread
import com.fluxtah.ask.app.commanding.commands.MaxCompletionTokens
import com.fluxtah.ask.app.commanding.commands.MaxPromptTokens
import com.fluxtah.ask.app.commanding.commands.SwitchThread
import com.fluxtah.askpluginsdk.logging.AskLogger
import com.fluxtah.askpluginsdk.logging.LogLevel

data class CommandEntry(val name: String, val description: String, val command: (List<String>) -> Command)

class CommandFactory(
    private val askLogger: AskLogger,
    private val responsePrinter: AskResponsePrinter,
    private val assistantsApi: AssistantsApi,
    private val assistantRegistry: AssistantRegistry,
    private val assistantInstallRepository: AssistantInstallRepository,
    private val userProperties: UserProperties,
    private val threadRepository: ThreadRepository,
) {
    private val commands = mutableMapOf<String, CommandEntry>()

    init {
        registerCommand(
            name = "max-completion-tokens",
            description = "<number> - Set the max completion tokens value",
            command = {
                if (it.size != 1 || it.first().toIntOrNull() == null) {
                    UnknownCommand("Current max completion tokens: ${userProperties.getMaxCompletionTokens()}, to set a new value use /max-completion-tokens <number>")
                } else {
                    MaxCompletionTokens(userProperties, it.first().toInt())
                }
            }
        )

        registerCommand(
            name = "max-prompt-tokens",
            description = "<number> - Set the max prompt tokens value",
            command = {
                if (it.size != 1 || it.first().toIntOrNull() == null) {
                    UnknownCommand("Current max prompt tokens: ${userProperties.getMaxPromptTokens()}, to set a new value use /max-prompt-tokens <number>")
                } else {
                    MaxPromptTokens(userProperties, it.first().toInt())
                }
            }
        )

        registerCommand(
            name = "help",
            description = "Show this help",
            command = { Help(this, responsePrinter) }
        )
        registerCommand(
            name = "exit",
            description = "Exits ask",
            command = { Exit() })
        registerCommand(
            name = "clear",
            description = "Clears the screen",
            command = { Clear() }
        )
        registerCommand(
            name = "assistant-install",
            description = "<assistant-id> Installs an assistant",
            command = {
                if (it.size != 1) {
                    UnknownCommand("Invalid number of arguments for /assistant-install, expected an assistant ID following the command")
                } else {
                    InstallAssistant(assistantRegistry, assistantInstallRepository, it.first())
                }
            }
        )
        registerCommand(
            name = "assistant-uninstall",
            description = "<assistant-id> Uninstalls an assistant", command = {
                if (it.size != 1) {
                    UnknownCommand("Invalid number of arguments for /assistant-uninstall, expected an assistant ID following the command")
                } else {
                    UnInstallAssistant(assistantRegistry, assistantInstallRepository, it.first())
                }
            }
        )
        registerCommand(
            name = "assistant-list",
            description = "Displays all available assistants",
            command = { ListAssistants(assistantRegistry, assistantInstallRepository) }
        )
        registerCommand(
            name = "assistant-which",
            description = "Displays the current assistant thread",
            command = {
                WhichAssistant(
                    userProperties,
                    assistantRegistry,
                    assistantInstallRepository
                )
            }
        )
        registerCommand(
            name = "assistant-info",
            description = "<assistant-id> Displays info for the assistant",
            command = {
                if (it.size != 1) {
                    UnknownCommand("Invalid number of arguments for /assistant-info, expected a assistant ID following the command")
                } else {
                    GetAssistant(assistantRegistry, assistantInstallRepository, assistantsApi, it.first())
                }
            })
        registerCommand(
            name = "model",
            description = "<model-id> Set model override affecting all assistants (gpt-3.5-turbo-16k, gpt-4-turbo, etc.)",
            command = {
                if (it.size != 1) {
                    UnknownCommand("Invalid number of arguments for /model, expected a model ID following the command")
                } else {
                    SetModel(userProperties, it.first())
                }
            })
        registerCommand(
            name = "model-clear",
            description = "Clears the current model override",
            command = { ClearModel(userProperties) })
        registerCommand(
            name = "model-which",
            description = "Displays the current model override",
            command = { WhichModel(userProperties) })
        registerCommand(
            name = "thread-new",
            description = "Creates a new assistant thread",
            command = { args ->
                val title = if (args.isNotEmpty()) args.joinToString(" ") else null
                ThreadNew(assistantsApi, userProperties, threadRepository, title)
            })
        registerCommand(
            name = "thread-which",
            description = "Displays the current assistant thread",
            command = { WhichThread(userProperties) })
        registerCommand(
            name = "thread-info",
            description = "<thread-id> - Displays the assistant thread",
            command = {
                if (it.isEmpty()) {
                    GetThread(assistantsApi, userProperties, null)
                } else {
                    GetThread(assistantsApi, userProperties, it.first())
                }
            }
        )
        registerCommand(
            name = "thread-delete",
            description = "<thread-id> - Delete the thread by the given id",
            command = {
                if (it.isEmpty() || it.joinToString("").trim().isEmpty()) {
                    UnknownCommand("Invalid number of arguments for /thread-delete, expected a thread ID following the command")
                } else {
                    DeleteThread(assistantsApi, threadRepository, userProperties, it.first().trim())
                }
            }
        )
        registerCommand(
            name = "thread-list",
            description = "Lists all assistant threads",
            command = { ListThreads(userProperties, threadRepository) }
        )
        registerCommand(
            name = "thread-switch",
            description = "<thread-id> - Switches to the given thread",
            command = {
                if (it.size != 1) {
                    UnknownCommand("Invalid number of arguments for /thread-switch, expected a thread ID following the command")
                } else {
                    SwitchThread(assistantsApi, userProperties, threadRepository, it.first().trim())
                }
            }
        )
        registerCommand(
            name = "thread-rename",
            description = "<thread-id> <new-title> - Renames the given thread",
            command = {
                if (it.size != 2) {
                    UnknownCommand("Invalid number of arguments for /thread-rename, expected a thread ID and new title following the command")
                } else {
                    ThreadRename(threadRepository, it[0], it[1])
                }
            }
        )
        registerCommand(
            name = "thread-recall",
            description = "Recalls the current assistant thread messages (prints out message history)",
            command = { ThreadRecall(assistantsApi, userProperties) }
        )
        registerCommand(
            name = "message-list",
            description = "Lists all messages in the current assistant thread",
            command = { ListMessages(assistantsApi, userProperties) }
        )
        registerCommand(
            name = "run-list",
            description = "Lists all runs in the current assistant thread",
            command = { ListRuns(assistantsApi, userProperties) }
        )
        registerCommand(
            name = "run-step-list",
            description = "Lists all run steps in the current assistant thread",
            command = { ListRunSteps(assistantsApi, userProperties) })
        registerCommand(
            name = "http-log",
            description = "Displays the last 10 HTTP requests",
            command = { ShowHttpLog })
        registerCommand(
            name = "set-key",
            description = "<api-key> - Set your openai api key",
            command = {
                if (it.size != 1) {
                    UnknownCommand("Invalid number of arguments for /set-key, expected an API key following the command")
                } else {
                    SetOpenAiApiKey(userProperties, it.first())
                }
            }
        )
        registerCommand(
            name = "log-level",
            description = "<level> Set the log level (ERROR, DEBUG, INFO, OFF)",
            command = {
                if (it.size != 1) {
                    UnknownCommand("Invalid number of arguments for /log-level, expected a log level ERROR, DEBUG, INFO or OFF following the command, current log level: ${userProperties.getLogLevel()}")
                } else {
                    try {
                        SetLogLevel(userProperties, askLogger, LogLevel.valueOf(it.first().uppercase()))
                    } catch (e: IllegalArgumentException) {
                        UnknownCommand("Invalid log level: ${it.first()}")
                    }
                }
            })
        registerCommand(
            name = "exec",
            description = "<command> - Executes a shell command for convenience",
            command = {
                if (it.isEmpty()) {
                    UnknownCommand("Invalid number of arguments for /exec, expected a shell command following the command")
                } else {
                    ShellExec(responsePrinter, it.joinToString(" "))
                }
            }
        )
    }


    fun registerCommand(name: String, description: String, command: (List<String>) -> Command) {
        commands[name] = CommandEntry(name, description, command)
    }

    fun create(input: String): Command {
        val parts = input.drop(1).split(" ")
        return commands[parts[0]]?.command?.invoke(parts.drop(1)) ?: UnknownCommand("Unknown command: ${parts[0]}")
    }

    fun getCommands(): List<CommandEntry> {
        return commands.values.sortedBy { it.name }
    }
}