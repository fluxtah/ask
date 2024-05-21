/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.api

import com.fluxtah.ask.api.assistants.AssistantInstallRepository
import com.fluxtah.ask.api.assistants.AssistantRegistry
import com.fluxtah.ask.api.clients.openai.assistants.AssistantsApi
import com.fluxtah.ask.api.clients.openai.assistants.model.AssistantRun
import com.fluxtah.ask.api.clients.openai.assistants.model.AssistantRunStepDetails
import com.fluxtah.ask.api.clients.openai.assistants.model.AssistantRunStepDetails.ToolCalls.ToolCallDetails.FunctionToolCallDetails
import com.fluxtah.ask.api.clients.openai.assistants.model.Message
import com.fluxtah.ask.api.clients.openai.assistants.model.RunRequest
import com.fluxtah.ask.api.clients.openai.assistants.model.RunStatus
import com.fluxtah.ask.api.clients.openai.assistants.model.SubmitToolOutputsRequest
import com.fluxtah.ask.api.clients.openai.assistants.model.ToolOutput
import com.fluxtah.ask.api.clients.openai.assistants.model.TruncationStrategy
import com.fluxtah.ask.api.markdown.AnsiMarkdownRenderer
import com.fluxtah.ask.api.markdown.MarkdownParser
import com.fluxtah.ask.api.tools.fn.FunctionInvoker
import com.fluxtah.askpluginsdk.AssistantDefinition
import com.fluxtah.askpluginsdk.logging.AskLogger
import com.fluxtah.askpluginsdk.logging.LogLevel

data class RunDetails(
    val assistantId: String,
    val model: String? = null,
    val threadId: String,
    val prompt: String
)

sealed class RunResult {
    data class Complete(
        val runId: String,
        val responseText: String
    ) : RunResult()

    data class Error(val message: String) : RunResult()
}

class AssistantRunner(
    private val logger: AskLogger,
    private val assistantsApi: AssistantsApi,
    private val assistantRegistry: AssistantRegistry,
    private val assistantInstallRepository: AssistantInstallRepository,
    private val functionInvoker: FunctionInvoker,
) {
    suspend fun run(
        details: RunDetails,
        onRunStatusChanged: (RunStatus) -> Unit,
        onMessageCreation: (Message) -> Unit,
        truncationStrategy: TruncationStrategy? = null
    ): RunResult {
        val assistantDef = assistantRegistry.getAssistantById(details.assistantId)
            ?: return RunResult.Error("Assistant definition not found: $details.assistantId")

        val assistantInstallRecord = assistantInstallRepository.getAssistantInstallRecord(assistantDef.id)
            ?: return RunResult.Error("Assistant not installed: $details.assistantId, to install use /assistant-install ${details.assistantId}")

        val userMessage = assistantsApi.messages.createUserMessage(details.threadId, details.prompt)
        val createRun = assistantsApi.runs.createRun(
            details.threadId, RunRequest(
                assistantId = assistantInstallRecord.installId,
                model = details.model?.ifEmpty {
                    null
                },
                truncationStrategy = truncationStrategy
            )
        )

        processRun(assistantDef, createRun, details.threadId, onRunStatusChanged, onMessageCreation)

        val responseBuilder = StringBuilder()

        val lastMessage =
            assistantsApi.messages.listMessages(
                threadId = details.threadId,
                beforeId = userMessage.id
            ).data.firstOrNull()

        if (lastMessage != null) {
            if (userMessage.id != lastMessage.id) {
                val markdownParser = MarkdownParser(lastMessage.content.first().text.value)
                val ansiMarkdown = AnsiMarkdownRenderer().render(markdownParser.parse())
                responseBuilder.append("\u001B[0m")
                responseBuilder.append(ansiMarkdown)
            }
        }

        return RunResult.Complete(createRun.id, responseBuilder.toString())
    }

    private suspend fun processRun(
        assistantDef: AssistantDefinition,
        startRun: AssistantRun,
        currentThreadId: String,
        onRunStatusChanged: (RunStatus) -> Unit,
        onMessageCreation: (Message) -> Unit
    ) {
        var currentRun = startRun
        while (true) {
            currentRun = pollRunStatus(assistantsApi, currentThreadId, currentRun) { status ->
                onRunStatusChanged(status)
            }

            when (currentRun.status) {
                RunStatus.REQUIRES_ACTION -> {
                    currentRun = executeRunSteps(assistantDef, currentThreadId, currentRun, onMessageCreation)
                    onRunStatusChanged(currentRun.status)
                }

                // Polling states
                RunStatus.QUEUED,
                RunStatus.IN_PROGRESS,
                RunStatus.CANCELLING -> {
                    onRunStatusChanged(currentRun.status)
                }

                // Terminal states
                RunStatus.COMPLETED,
                RunStatus.FAILED,
                RunStatus.CANCELLED,
                RunStatus.EXPIRED,
                RunStatus.INCOMPLETE -> {
                    onRunStatusChanged(currentRun.status)
                    break
                }
            }
        }
    }

    private suspend fun executeRunSteps(
        assistantDef: AssistantDefinition,
        currentThreadId: String,
        run: AssistantRun,
        onMessageCreation: (Message) -> Unit
    ): AssistantRun {
        val steps = assistantsApi.runs.listRunSteps(currentThreadId, run.id)

        steps.data.forEach { step ->
            when (val details = step.stepDetails) {
                is AssistantRunStepDetails.MessageCreation -> {
                    val message = assistantsApi.messages.getMessage(step.threadId, details.messageCreation.messageId)
                    onMessageCreation(message)
                }

                is AssistantRunStepDetails.ToolCalls -> {
                    return executeTools(assistantDef, currentThreadId, run, details)
                }
            }
        }
        return run
    }

    private suspend fun executeTools(
        assistant: AssistantDefinition,
        currentThreadId: String,
        run: AssistantRun,
        details: AssistantRunStepDetails.ToolCalls
    ): AssistantRun {
        val toolOutputs = mutableListOf<ToolOutput>()

        details.toolCalls.forEach { toolCall ->
            when (toolCall) {
                is FunctionToolCallDetails -> {
                    logger.log(
                        LogLevel.DEBUG,
                        "[Exec Fun] ${toolCall.function.name}: ${toolCall.function.arguments.take(200)}..."
                    )
                    val result = functionInvoker.invokeFunction(assistant.functions, toolCall)
                    toolOutputs.add(
                        ToolOutput(
                            toolCall.id,
                            result
                        )
                    )
                    logger.log(LogLevel.DEBUG, "[Fun Result] $result")
                }
            }
        }

        return submitToolOutputs(toolOutputs, currentThreadId, run)
    }

    private suspend fun submitToolOutputs(
        toolOutputs: MutableList<ToolOutput>,
        currentThreadId: String,
        run: AssistantRun
    ) = if (toolOutputs.isNotEmpty()) {
        assistantsApi.runs.submitToolOutputs(
            currentThreadId,
            run.id,
            SubmitToolOutputsRequest(toolOutputs)
        )
    } else {
        run
    }
}