package com.fluxtah.ask.api

import com.fluxtah.ask.api.clients.openai.assistants.model.TruncationStrategy

data class RunDetails(
    val assistantId: String,
    val model: String? = null,
    val threadId: String,
    val prompt: String,
    val maxPromptTokens: Int? = null,
    val maxCompletionTokens: Int? = null,
    val truncationStrategy: TruncationStrategy? = null
)