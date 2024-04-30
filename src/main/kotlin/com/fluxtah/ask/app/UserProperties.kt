package com.fluxtah.ask.app

import com.fluxtah.ask.api.store.PropertyStore

class UserProperties(private val store: PropertyStore) {
    companion object {
        const val THREAD_ID = "threadId"
        const val RUN_ID = "runId"
        const val OPENAI_API_KEY = "openaiApiKey"
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

    fun load() {
        store.load()
    }

    fun save() {
        store.save()
    }
}