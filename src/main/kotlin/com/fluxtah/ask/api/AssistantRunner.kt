package com.fluxtah.ask.api

import com.fluxtah.ask.api.assistants.AssistantInstallRepository
import com.fluxtah.ask.api.assistants.AssistantRegistry
import com.fluxtah.ask.api.clients.openai.assistants.AssistantsApi
import com.fluxtah.ask.api.clients.openai.assistants.model.AssistantRun
import com.fluxtah.ask.api.clients.openai.assistants.model.AssistantRunStepDetails
import com.fluxtah.ask.api.clients.openai.assistants.model.RunRequest
import com.fluxtah.ask.api.clients.openai.assistants.model.RunStatus
import com.fluxtah.ask.api.clients.openai.assistants.model.SubmitToolOutputsRequest
import com.fluxtah.ask.api.clients.openai.assistants.model.ToolOutput
import com.fluxtah.ask.api.tools.fn.FunctionInvoker
import com.fluxtah.ask.api.printers.AskResponsePrinter
import com.fluxtah.ask.app.UserProperties
import com.fluxtah.askpluginsdk.AssistantDefinition
import com.fluxtah.askpluginsdk.logging.AskLogger
import com.fluxtah.askpluginsdk.logging.LogLevel

class AssistantRunner(
    private val logger: AskLogger,
    private val userProperties: UserProperties,
    private val assistantsApi: AssistantsApi,
    private val assistantRegistry: AssistantRegistry,
    private val assistantInstallRepository: AssistantInstallRepository,
    private val functionInvoker: FunctionInvoker,
    private val responsePrinter: AskResponsePrinter
) {
    suspend fun run(assistantId: String, threadId: String, prompt: String) {
        val assistantDef = assistantRegistry.getAssistantById(assistantId)

        if (assistantDef == null) {
            responsePrinter.println("Assistant definition not found: $assistantId")
            return
        }

        val assistantInstallRecord = assistantInstallRepository.getAssistantInstallRecord(assistantDef.id)

        if (assistantInstallRecord == null) {
            responsePrinter.println("Assistant not installed: $assistantId, to install use /assistant-install $assistantId")
            return
        }

        val userMessage = assistantsApi.messages.createUserMessage(threadId, prompt)
        val createRun = assistantsApi.runs.createRun(
            threadId, RunRequest(
                assistantId = assistantInstallRecord.installId,
                model = userProperties.getModel().ifEmpty {
                    null
                },
            )
        )
        userProperties.setRunId(createRun.id)
        userProperties.setAssistantId(assistantId)
        userProperties.save()

        processRun(assistantDef, createRun, threadId)

        val lastMessage =
            assistantsApi.messages.listMessages(
                threadId = threadId,
                beforeId = userMessage.id
            ).data.firstOrNull()
        if (lastMessage != null) {
            if (userMessage.id != lastMessage.id) {
                responsePrinter.println(lastMessage.content.first().text.value)
            }
        }
    }

    private suspend fun processRun(
        assistantDef: AssistantDefinition,
        startRun: AssistantRun,
        currentThreadId: String
    ) {
        var currentRun = startRun
        responsePrinter.print(" ")
        while (pollRunStatus(assistantsApi, currentThreadId, startRun) { status ->
                responsePrinter.print(" > $status")
            }.status == RunStatus.REQUIRES_ACTION) {
            currentRun = executeRunSteps(assistantDef, currentThreadId, currentRun)
        }

        responsePrinter.println(" > ${currentRun.status}")
        responsePrinter.println()
    }

    private suspend fun executeRunSteps(
        assistantDef: AssistantDefinition,
        currentThreadId: String,
        run: AssistantRun
    ): AssistantRun {
        val steps = assistantsApi.runs.listRunSteps(currentThreadId, run.id)

        steps.data.forEach { step ->
            when (val details = step.stepDetails) {
                is AssistantRunStepDetails.MessageCreation -> {}
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
                is AssistantRunStepDetails.ToolCalls.ToolCallDetails.FunctionToolCallDetails -> {
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