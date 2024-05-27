package com.fluxtah.ask.app

import com.fluxtah.ask.api.AssistantRunner
import com.fluxtah.ask.api.RunDetails
import com.fluxtah.ask.api.RunResult
import com.fluxtah.ask.api.RunRetryDetails
import com.fluxtah.ask.api.ansi.blue
import com.fluxtah.ask.api.ansi.green
import com.fluxtah.ask.api.clients.openai.assistants.AssistantsApi
import com.fluxtah.ask.api.clients.openai.assistants.model.AssistantRunStepDetails
import com.fluxtah.ask.api.clients.openai.assistants.model.Message
import com.fluxtah.ask.api.clients.openai.assistants.model.RunStatus
import com.fluxtah.ask.api.printers.AskResponsePrinter
import com.fluxtah.ask.app.commanding.CommandFactory
import com.fluxtah.ask.app.commanding.commands.Command
import com.fluxtah.askpluginsdk.logging.AskLogger
import com.fluxtah.askpluginsdk.logging.LogLevel
import kotlinx.coroutines.runBlocking

class InputHandler(
    private val commandFactory: CommandFactory,
    private val responsePrinter: AskResponsePrinter,
    private val logger: AskLogger,
    private val userProperties: UserProperties,
    private val assistantRunner: AssistantRunner,
    private val assistantsApi: AssistantsApi
) {
    fun handleInput(input: String) {
        if (input.isEmpty()) {
            return
        }

        try {
            when {
                input.startsWith("/") -> {
                    val command = commandFactory.create(input)
                    if (runCommand(command)) return
                }

                input.startsWith(":") -> { // Alias for /exec
                    val command = commandFactory.create("/exec ${input.drop(1)}")
                    if (runCommand(command)) return
                }

                else -> {
                    runAssistantOnMessageThread(input)
                }
            }
        } catch (e: Exception) {
            responsePrinter.println("Error: ${e.message}, run with /log-level ERROR for more info")
            logger.log(LogLevel.ERROR, "Error: ${e.stackTraceToString()}")
        }
    }

//    fun retry() {
//        val runId = userProperties.getRunId()
//        if (runId.isEmpty()) {
//            responsePrinter.println("Nothing to retry")
//            return
//        }
//
//        val threadId: String = userProperties.getThreadId()
//        if (threadId.isEmpty()) {
//            responsePrinter.println("No thread to retry in")
//            return
//        }
//
//        runBlocking {
//            val run = assistantsApi.runs.getRun(threadId, runId)
//
//            if (run.status == RunStatus.REQUIRES_ACTION) {
//                assistantRunner.retryRun(
//                    details = RunRetryDetails(
//                        assistantId = run.assistantId,
//                        threadId = threadId,
//                        runId = runId,
//                    ),
//                    onRunStatusChanged = { status ->
//                        onRunStatusChanged(" ", status)
//                    },
//                    onMessageCreation = { message ->
//                        onMessageCreation(message)
//                    },
//                    onExecuteTool = { toolCallDetails, message ->
//                        onExecuteTool(toolCallDetails)
//                    }
//                )
//            }
//        }
//    }

    private fun runAssistantOnMessageThread(input: String) {
        val currentThreadId = userProperties.getThreadId()

        if (currentThreadId.isEmpty()) {
            responsePrinter.println("You need to create a thread first. Use /thread-new")
            return
        }

        if (!input.startsWith("@") && userProperties.getAssistantId().isEmpty()) {
            responsePrinter.println("You need to address an assistant with @assistant-id <prompt>, to see available assistants use /assistant-list")
            return
        }

        val assistantId = getNamedAssistantIdOrLast(input)
        responsePrinter.println()

        val loadingChars = listOf("|", "/", "-", "\\")
        var loadingCharIndex = 0

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
                onRunStatusChanged = { status ->
                    loadingCharIndex = (loadingCharIndex + 1) % loadingChars.size
                    onRunStatusChanged(loadingChars[loadingCharIndex], status)
                },
                onMessageCreation = { message ->
                    onMessageCreation(message)
                },
                onExecuteTool = { toolCallDetails, message ->
                    onExecuteTool(toolCallDetails)
                }
            )

            when (result) {
                is RunResult.Complete -> {
                    userProperties.setRunId(result.runId)
                    userProperties.setAssistantId(assistantId)
                    userProperties.save()

                    responsePrinter.println()
                    responsePrinter.println(result.responseText)
                }

                is RunResult.Error -> {
                    responsePrinter.println(result.message)
                }
            }
        }
    }

    private fun onExecuteTool(toolCallDetails: AssistantRunStepDetails.ToolCalls.ToolCallDetails.FunctionToolCallDetails) {
        responsePrinter.print("\u001b[1A\u001b[2K")
        responsePrinter.println(" ${green("==>")} ${blue(toolCallDetails.function.name)}(${toolCallDetails.function.arguments}")
        responsePrinter.println()
        responsePrinter.println()
    }

    private fun onMessageCreation(message: Message) {
        responsePrinter.print("\u001b[1A\u001b[2K")
        responsePrinter.println(message.content.joinToString(" ") { it.text.value })
        responsePrinter.println()
        responsePrinter.println()
    }

    private fun onRunStatusChanged(
        loadingIndicator: String,
        status: RunStatus
    ) {
        responsePrinter.print("\u001b[1A\u001b[2K")
        val indicator = when (status) {
            RunStatus.FAILED,
            RunStatus.CANCELLED,
            RunStatus.EXPIRED -> "x"

            RunStatus.COMPLETED -> green("✔")
            else -> blue(loadingIndicator)
        }

        responsePrinter.println(" $indicator $status")
    }

    private fun getNamedAssistantIdOrLast(input: String) = if (input.startsWith("@")) {
        val parts = input.split(" ")
        val assistantId = parts[0].substring(1)
        parts.drop(1).joinToString(" ")
        assistantId
    } else {
        userProperties.getAssistantId()
    }

    private fun runCommand(command: Command): Boolean {
        if (command.requiresApiKey) {
            if (userProperties.getOpenaiApiKey().isEmpty()) {
                responsePrinter.println("You need to set an OpenAI API key first! with /set-key <api-key>")
                return true
            }
        }
        runBlocking {
            command.execute()
        }
        return false
    }
}