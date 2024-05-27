/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.api

import com.fluxtah.ask.api.assistants.AssistantInstallRecord
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
import com.fluxtah.ask.api.markdown.AnsiMarkdownRenderer
import com.fluxtah.ask.api.markdown.MarkdownParser
import com.fluxtah.ask.api.tools.fn.FunctionInvoker
import com.fluxtah.askpluginsdk.AssistantDefinition
import com.fluxtah.askpluginsdk.logging.AskLogger
import com.fluxtah.askpluginsdk.logging.LogLevel

class AssistantRunner(
    private val logger: AskLogger,
    private val assistantsApi: AssistantsApi,
    private val assistantRegistry: AssistantRegistry,
    private val assistantInstallRepository: AssistantInstallRepository,
    private val functionInvoker: FunctionInvoker = FunctionInvoker(),
) {
    suspend fun run(
        details: RunDetails,
        onRunStatusChanged: (RunStatus) -> Unit,
        onMessageCreation: (Message) -> Unit,
        onExecuteTool: (FunctionToolCallDetails, String) -> Unit
    ): RunResult {
        val assistantDef = assistantRegistry.getAssistantById(details.assistantId)
            ?: return RunResult.Error("Assistant definition not found: ${details.assistantId}")

        val assistantInstallRecord = assistantInstallRepository.getAssistantInstallRecord(assistantDef.id)
            ?: return RunResult.Error("Assistant not installed: ${details.assistantId}, to install use /assistant-install ${details.assistantId}")

        val userMessage = assistantsApi.messages.createUserMessage(details.threadId, details.prompt)

        val run = createRun(details, assistantInstallRecord)

        processRun(assistantDef, run, details.threadId, onRunStatusChanged, onMessageCreation, onExecuteTool)

        val responseBuilder = buildResponseText(details.threadId, userMessage)

        return RunResult.Complete(run.id, responseBuilder.toString())
    }

    suspend fun retryRun(
        details: RunRetryDetails,
        onRunStatusChanged: (RunStatus) -> Unit,
        onMessageCreation: (Message) -> Unit,
        onExecuteTool: (FunctionToolCallDetails, String) -> Unit
    ): RunResult {
        val run = assistantsApi.runs.getRun(details.threadId, details.runId)

        if (run.status != RunStatus.REQUIRES_ACTION) {
            return RunResult.Error("Retry is only supported for runs in status REQUIRES_ACTION, last run was in status ${run.status}")
        }

        val assistantDef = assistantRegistry.getAssistantById(run.assistantId)
            ?: return RunResult.Error("Assistant definition not found: ${run.assistantId}")

        processRun(assistantDef, run, details.threadId, onRunStatusChanged, onMessageCreation, onExecuteTool)

        // TODO we need to get the last user message
        val lastMessage =
            assistantsApi.messages.listMessages(
                threadId = details.threadId,
            ).data.firstOrNull { it.role == "user" }

        if (lastMessage == null) {
            return RunResult.Error("No user message found in thread")
        }

        val responseBuilder = buildResponseText(details.threadId, lastMessage)

        return RunResult.Complete(run.id, responseBuilder.toString())
    }

    private suspend fun buildResponseText(
        threadId: String,
        userMessage: Message
    ): StringBuilder {
        val responseBuilder = StringBuilder()

        val lastMessage =
            assistantsApi.messages.listMessages(
                threadId = threadId,
                beforeId = userMessage.id
            ).data.firstOrNull()

        if (lastMessage != null) {
            if (userMessage.id != lastMessage.id) {
                val markdownParser = MarkdownParser(lastMessage.content.first().text.value)
                val renderedMarkdown = AnsiMarkdownRenderer().render(markdownParser.parse())
                responseBuilder.append("\u001B[0m")
                responseBuilder.append(renderedMarkdown)
            }
        }
        return responseBuilder
    }

    private suspend fun createRun(
        details: RunDetails,
        assistantInstallRecord: AssistantInstallRecord
    ) = assistantsApi.runs.createRun(
        details.threadId, RunRequest(
            assistantId = assistantInstallRecord.installId,
            model = details.model?.ifEmpty {
                null
            },
            truncationStrategy = details.truncationStrategy,
            maxCompletionTokens = details.maxCompletionTokens,
            maxPromptTokens = details.maxPromptTokens
        )
    )

    private suspend fun processRun(
        assistantDef: AssistantDefinition,
        startRun: AssistantRun,
        currentThreadId: String,
        onRunStatusChanged: (RunStatus) -> Unit,
        onMessageCreation: (Message) -> Unit,
        onExecuteTool: (FunctionToolCallDetails, String) -> Unit
    ) {
        var currentRun = startRun
        val messagesSeen = mutableSetOf<String>()
        while (true) {
            currentRun = pollRunStatus(assistantsApi, currentThreadId, currentRun) { status ->
                onRunStatusChanged(status)
            }

            when (currentRun.status) {
                RunStatus.REQUIRES_ACTION -> {
                    currentRun =
                        executeRunSteps(
                            assistantDef,
                            currentThreadId,
                            currentRun,
                            onMessageCreation,
                            onExecuteTool,
                            messagesSeen
                        )
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
        threadId: String,
        run: AssistantRun,
        onMessageCreation: (Message) -> Unit,
        onExecuteTool: (FunctionToolCallDetails, String) -> Unit,
        messagesSeen: MutableSet<String>
    ): AssistantRun {
        val steps = assistantsApi.runs.listRunSteps(threadId, run.id)

        val toolOutputs = mutableListOf<ToolOutput>()

        logger.log(LogLevel.DEBUG, "[Run Steps] ${steps.data.size} steps")
        steps.data.map { it.stepDetails }.filterIsInstance<AssistantRunStepDetails.MessageCreation>()
            .forEach { details ->
                if (!messagesSeen.contains(details.messageCreation.messageId)) {
                    logger.log(LogLevel.DEBUG, "[Message Creation] ${details.messageCreation.messageId}")
                    val message = assistantsApi.messages.getMessage(threadId, details.messageCreation.messageId)
                    onMessageCreation(message)
                    messagesSeen.add(details.messageCreation.messageId)
                }
            }

        steps.data.map { it.stepDetails }.filterIsInstance<AssistantRunStepDetails.ToolCalls>().first { details ->
            logger.log(LogLevel.DEBUG, "[Tool Calls] ${details.toolCalls.size} calls")
            toolOutputs.addAll(executeTools(assistantDef, details, onExecuteTool))
            return submitToolOutputs(toolOutputs, threadId, run)
        }


        return run
    }

    private fun executeTools(
        assistant: AssistantDefinition,
        details: AssistantRunStepDetails.ToolCalls,
        onExecuteTool: (FunctionToolCallDetails, String) -> Unit
    ): List<ToolOutput> {
        val toolOutputs = mutableListOf<ToolOutput>()

        details.toolCalls.forEach { toolCall ->
            when (toolCall) {
                is FunctionToolCallDetails -> {
                    logger.log(
                        LogLevel.DEBUG,
                        "[Exec Fun] ${toolCall.function.name}: ${toolCall.function.arguments.take(200)}..."
                    )
                    val result = functionInvoker.invokeFunction(assistant.functions, toolCall)
                    onExecuteTool(toolCall, result)
                    toolOutputs.add(
                        ToolOutput(
                            toolCall.id,
                            result
                        )
                    )
                    logger.log(LogLevel.DEBUG, "[Fun Result] ${result.take(200)}...")
                }
            }
        }

        return toolOutputs
    }

    private suspend fun submitToolOutputs(
        toolOutputs: List<ToolOutput>,
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