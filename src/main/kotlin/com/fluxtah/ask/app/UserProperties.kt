/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.app

import com.fluxtah.ask.api.store.PropertyStore

class UserProperties(private val store: PropertyStore) {
    companion object {
        const val THREAD_ID = "threadId"
        const val RUN_ID = "runId"
        const val OPENAI_API_KEY = "openaiApiKey"
        const val MODEL = "model"
        const val ASSISTANT_ID = "assistantId"
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

    fun getAssistantId(): String {
        return store.getProperty(ASSISTANT_ID)
    }

    fun setAssistantId(assistantId: String) {
        store.setProperty(ASSISTANT_ID, assistantId)
    }

    fun load() {
        store.load()
    }

    fun save() {
        store.save()
    }
}