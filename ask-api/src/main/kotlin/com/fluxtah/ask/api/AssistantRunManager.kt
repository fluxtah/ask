package com.fluxtah.ask.api

import com.fluxtah.ask.api.clients.openai.assistants.model.AssistantRunStepDetails
import com.fluxtah.ask.api.clients.openai.assistants.model.Message
import com.fluxtah.ask.api.clients.openai.assistants.model.RunStatus
import com.fluxtah.ask.api.store.user.UserProperties
import kotlinx.coroutines.runBlocking

class AssistantRunManager(
    private val assistantRunner: AssistantRunner,
    private val userProperties: UserProperties
) {
    var onStatusChanged: ((RunManagerStatus) -> Unit)? = null

    fun runAssistant(input: String) {
        val currentThreadId = userProperties.getThreadId()

        if (currentThreadId.isEmpty()) {
            onStatusChanged?.invoke(
                RunManagerStatus.Error(
                    "You need to create a thread first. Use /thread-new",
                    RunManagerStatus.ErrorType.ThreadNotSet
                )
            )
            return
        }

        if (!input.startsWith("@") && userProperties.getAssistantId().isEmpty()) {
            onStatusChanged?.invoke(
                RunManagerStatus.Error(
                    "You need to address an assistant with @assistant-id <prompt>, to see available assistants use /assistant-list",
                    RunManagerStatus.ErrorType.TargetAssistantNotSet
                )
            )
            return
        }

        val assistantId = getNamedAssistantIdOrLast(input)

        onStatusChanged?.invoke(RunManagerStatus.BeforeBeginRun)

        runBlocking {
            val result = assistantRunner.run(
                details = RunDetails(
                    assistantId = assistantId,
                    threadId = currentThreadId,
                    model = userProperties.getModel(),
                    prompt = input,
                    maxPromptTokens = userProperties.getMaxPromptTokensOrNull(),
                    maxCompletionTokens = userProperties.getMaxCompletionTokensOrNull(),
                    truncationStrategy = userProperties.getTruncationStrategyOrNull()
                ),
                onRunStatusChanged = ::onRunStatusChanged,
                onMessageCreation = ::onMessageCreation,
                onExecuteTool = { toolCallDetails ->
                    onExecuteTool(toolCallDetails)
                }
            )

            handleRunResult(result, assistantId)
        }
    }

    private fun handleRunResult(result: RunResult, assistantId: String) {
        when (result) {
            is RunResult.Complete -> {
                userProperties.setRunId(result.runId)
                userProperties.setAssistantId(assistantId)
                userProperties.save()

                onStatusChanged?.invoke(RunManagerStatus.Response(result.responseText))
            }

            is RunResult.Error -> {
                onStatusChanged?.invoke(RunManagerStatus.Error(result.message, RunManagerStatus.ErrorType.Unknown))
            }
        }
    }

    fun recoverRun() {
        val runId = userProperties.getRunId()
        if (runId.isEmpty()) {
            onStatusChanged?.invoke(
                RunManagerStatus.Error(
                    "No run to recover",
                    RunManagerStatus.ErrorType.NoRunToRecover
                )
            )
            return
        }

        val threadId: String = userProperties.getThreadId()
        if (threadId.isEmpty()) {
            onStatusChanged?.invoke(
                RunManagerStatus.Error(
                    "No thread to recover in",
                    RunManagerStatus.ErrorType.NoThreadToRecoverIn
                )
            )
            return
        }

        val result = runBlocking {
            assistantRunner.retryRun(
                details = RunRetryDetails(
                    threadId = threadId,
                    runId = runId,
                ),
                onRunStatusChanged = ::onRunStatusChanged,
                onMessageCreation = ::onMessageCreation,
                onExecuteTool = { toolCallDetails ->
                    onExecuteTool(toolCallDetails)
                }
            )
        }
        handleRunResult(result, userProperties.getAssistantId())
    }

    private fun onExecuteTool(toolCallDetails: AssistantRunStepDetails.ToolCalls.ToolCallDetails.FunctionToolCallDetails) {
        onStatusChanged?.invoke(RunManagerStatus.ToolCall(toolCallDetails))
    }

    private fun onMessageCreation(message: Message) {
        onStatusChanged?.invoke(RunManagerStatus.MessageCreated(message))
    }

    private fun onRunStatusChanged(status: RunStatus) {
        onStatusChanged?.invoke(RunManagerStatus.RunStatusChanged(status))
    }

    private fun getNamedAssistantIdOrLast(input: String) = if (input.startsWith("@")) {
        val parts = input.split(" ")
        val assistantId = parts[0].substring(1)
        parts.drop(1).joinToString(" ")
        assistantId
    } else {
        userProperties.getAssistantId()
    }
}