package com.fluxtah.ask.api

import com.fluxtah.ask.api.clients.openai.assistants.model.AssistantRunStepDetails
import com.fluxtah.ask.api.clients.openai.assistants.model.Message
import com.fluxtah.ask.api.clients.openai.assistants.model.RunStatus

sealed class RunManagerStatus {
    data object BeforeBeginRun : RunManagerStatus()
    data class MessageCreated(val message: Message) : RunManagerStatus()
    data class ToolCall(val details: AssistantRunStepDetails.ToolCalls.ToolCallDetails.FunctionToolCallDetails) :
        RunManagerStatus()

    data class Response(val response: String) : RunManagerStatus()
    data class Error(val message: String, val type: ErrorType) : RunManagerStatus()
    data class RunStatusChanged(val runStatus: RunStatus) : RunManagerStatus()

    enum class ErrorType {
        Unknown,
        ThreadNotSet,
        TargetAssistantNotSet,
        NoRunToRecover,
        NoThreadToRecoverIn
    }
}