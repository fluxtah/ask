package com.fluxtah.ask.app.commands

import com.fluxtah.ask.api.assistants.AssistantDefinition
import com.fluxtah.ask.api.assistants.AssistantInstallRepository
import com.fluxtah.ask.api.assistants.AssistantRegistry
import com.fluxtah.ask.api.clients.openai.assistants.AssistantsApi
import com.fluxtah.ask.api.clients.openai.assistants.HTTP_LOG
import com.fluxtah.ask.api.clients.openai.assistants.model.*
import com.fluxtah.ask.app.UserProperties
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*

sealed class Command {
    abstract suspend fun execute()

    class UnknownCommand(private val message: String) : Command() {
        override suspend fun execute() {
            println(message)
        }
    }

    data object Help : Command() {
        override suspend fun execute() {
            println("Help: List of available commands...")
            println("/exit - Exits the application")
            println("/assistant-list - Displays all available assistants")
            println("/assistant-install <assistant-id> - Installs an assistant")
            println("/assistant-info <assistant-id> - Displays the assistant")
            println("/thread - Creates a new assistant thread")
            println("/thread-which - Displays the current assistant thread")
            println("/thread-list - Lists all assistant threads")
            println("/message-list - Lists all messages in the current assistant thread")
            println("/run-list - Lists all runs in the current assistant thread")
            println("/run-step-list - Lists all run steps in the current assistant thread")
            println("/http-log - Displays the last 10 HTTP requests")
            println("/set-key <api-key> - Set your openai api key")
        }
    }

    data object Exit : Command() {
        override suspend fun execute() {
            println("Exiting the application...")
            System.exit(0)
        }
    }

    class SetOpenAiApiKey(private val userProperties: UserProperties, private val apiKey: String) : Command() {
        override suspend fun execute() {
            userProperties.setOpenAiApiKey(apiKey)
            userProperties.save()
        }
    }

    class CreateAssistantThread(private val assistantsApi: AssistantsApi, private val userProperties: UserProperties) :
        Command() {
        override suspend fun execute() {
            val thread = assistantsApi.threads.createThread()
            println("Created thread: ${thread.id} at ${Date(thread.createdAt)}")
            userProperties.setThreadId(thread.id)
            userProperties.save()
        }
    }

    class WhichThread(private val userProperties: UserProperties) : Command() {
        override suspend fun execute() {
            println("Current thread: ${userProperties.getThreadId().ifEmpty { "None" }}")
        }
    }

    class ListThreads(private val assistantsApi: AssistantsApi) : Command() {
        override suspend fun execute() {
            // TODO currently its not possible to list threads though the API should be up soon
            println(assistantsApi.threads.listThreads())
        }
    }

    class GetThread(private val assistantsApi: AssistantsApi, private val userProperties: UserProperties, private val threadId: String? = null) : Command() {
        override suspend fun execute() {
            val actualThread = threadId ?: userProperties.getThreadId().ifEmpty { null }

            if (actualThread == null) {
                println("You need to create a thread first. Use /thread-new or pass a thread as the first argument")
                return
            }
            println(JSON.encodeToString<AssistantThread>(assistantsApi.threads.getThread(actualThread)))
        }
    }

    class ListMessages(private val assistantsApi: AssistantsApi, private val userProperties: UserProperties) :
        Command() {
        override suspend fun execute() {
            val threadId = userProperties.getThreadId()
            if (threadId.isEmpty()) {
                println("You need to create a thread first. Use /thread-new")
                return
            }
            println(JSON.encodeToString<AssistantMessageList>(assistantsApi.messages.listMessages(threadId)))
        }
    }

    class ListRuns(private val assistantsApi: AssistantsApi, private val userProperties: UserProperties) : Command() {
        override suspend fun execute() {
            val threadId = userProperties.getThreadId()
            if (threadId.isEmpty()) {
                println("You need to create a thread first. Use /thread-new")
                return
            }
            assistantsApi.runs.listRuns(threadId).data.forEach {
                println("${it.id}, created: ${Date(it.createdAt)}, status: ${it.status}, last error: ${it.lastError}")
            }
        }
    }

    class ListRunSteps(private val assistantsApi: AssistantsApi, private val userProperties: UserProperties) :
        Command() {
        override suspend fun execute() {
            val threadId = userProperties.getThreadId()
            if (threadId.isEmpty()) {
                println("You need to create a thread first. Use /thread-new")
                return
            }
            val runId = userProperties.getRunId()
            if (runId.isEmpty()) {
                println("You need to create a run first. Use /run-new")
                return
            }

            println(JSON.encodeToString<AssistantRunStepList>(assistantsApi.runs.listRunSteps(threadId, runId)))
        }
    }

    class GetAssistant(private val assistantsApi: AssistantsApi, private val assistantId: String) :
        Command() {
        override suspend fun execute() {
            println(JSON.encodeToString<Assistant>(assistantsApi.assistants.getAssistant(assistantId)))
        }
    }

    class ListAssistants(private val assistantRegistry: AssistantRegistry, private val assistantInstallRepository: AssistantInstallRepository) : Command() {
        override suspend fun execute() {
            val installedAssistants = assistantInstallRepository.getAssistantInstallRecords()
            assistantRegistry.getAssistants().forEach {
                println("@${it.id} - ${it.name} ${it.version}, installed: ${installedAssistants.find { record -> record.id == it.id } != null}")
            }
        }
    }

    class InstallAssistant(private val assistantRegistry: AssistantRegistry, private val assistantInstallRepository: AssistantInstallRepository, val assistantId: String) : Command() {
        override suspend fun execute() {
            val def = assistantRegistry.getAssistantById(assistantId)

            if (def == null) {
                println("Assistant not found: $assistantId")
                return
            }

            val assistantInstallRecord = assistantInstallRepository.install(def)

            println("Installed assistant: @${def.id} ${def.version} as ${assistantInstallRecord.installId}")
        }

    }

    data object ShowHttpLog : Command() {
        override suspend fun execute() {
            HTTP_LOG.forEach {
                println(it)
            }
        }
    }
}

private val JSON = Json {
    isLenient = true
    prettyPrint = true
}