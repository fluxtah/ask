/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.app

import com.fluxtah.ask.api.clients.openai.assistants.model.TruncationStrategy
import com.fluxtah.ask.api.store.PropertyStore
import com.fluxtah.askpluginsdk.logging.LogLevel

class UserProperties(private val store: PropertyStore) {
    companion object {
        const val THREAD_ID = "threadId"
        const val RUN_ID = "runId"
        const val OPENAI_API_KEY = "openaiApiKey"
        const val MODEL = "model"
        const val ASSISTANT_ID = "assistantId"
        const val MAX_PROMPT_TOKENS = "maxPromptTokens"
        const val MAX_COMPLETION_TOKENS = "maxCompletionTokens"
        const val LOG_LEVEL = "logLevel"
        const val TRUNCATE_LAST_MESSAGES = "truncateLastMessages"
    }

    fun getThreadId(): String {
        return store.getProperty(THREAD_ID)
    }

    fun setThreadId(threadId: String) {
        store.setProperty(THREAD_ID, threadId)
    }

    fun getRunId(): String {
        return store.getProperty(RUN_ID)
    }

    fun setRunId(runId: String) {
        store.setProperty(RUN_ID, runId)
    }

    fun getOpenaiApiKey(): String {
        return store.getProperty(OPENAI_API_KEY)
    }

    fun setOpenAiApiKey(openaiApiKey: String) {
        store.setProperty(OPENAI_API_KEY, openaiApiKey)
    }

    fun getModel(): String {
        return store.getProperty(MODEL)
    }

    fun setModel(model: String) {
        store.setProperty(MODEL, model)
    }

    fun getMaxCompletionTokens(): Int {
        return store.getProperty(MAX_COMPLETION_TOKENS, "0").toInt()
    }

    fun setMaxCompletionTokens(maxCompletionTokens: Int) {
        store.setProperty(MAX_COMPLETION_TOKENS, maxCompletionTokens.toString())
    }

    fun getMaxPromptTokens(): Int {
        return store.getProperty(MAX_PROMPT_TOKENS, "0").toInt()
    }

    fun setMaxPromptTokens(maxPromptTokens: Int) {
        store.setProperty(MAX_PROMPT_TOKENS, maxPromptTokens.toString())
    }

    fun getAssistantId(): String {
        return store.getProperty(ASSISTANT_ID)
    }

    fun setAssistantId(assistantId: String) {
        store.setProperty(ASSISTANT_ID, assistantId)
    }

    fun getLogLevel(): LogLevel {
        return LogLevel.valueOf(store.getProperty(LOG_LEVEL, LogLevel.OFF.name))
    }

    fun setLogLevel(logLevel: LogLevel) {
        store.setProperty(LOG_LEVEL, logLevel.name)
    }

    fun getTruncateLastMessages(): Int {
        return store.getProperty(TRUNCATE_LAST_MESSAGES, "0").toInt()
    }

    fun setTruncateLastMessages(value: Int) {
        store.setProperty(TRUNCATE_LAST_MESSAGES, value.toString())
    }

    fun getMaxCompletionTokensOrNull() = if (getMaxCompletionTokens() > 0) {
        getMaxCompletionTokens()
    } else {
        null
    }

    fun getMaxPromptTokensOrNull() = if (getMaxPromptTokens() > 0) {
        getMaxPromptTokens()
    } else {
        null
    }

    fun getTruncationStrategyOrNull() = if (getTruncateLastMessages() > 0) {
        TruncationStrategy.LastMessages(getTruncateLastMessages())
    } else {
        null
    }

    fun load() {
        store.load()
    }

    fun save() {
        store.save()
    }

}
