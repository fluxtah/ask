package com.fluxtah.ask.app.commanding.commands

import com.fluxtah.ask.api.UserProperties
import com.fluxtah.ask.api.clients.openai.assistants.AssistantsApi
import com.fluxtah.ask.app.green
import com.fluxtah.ask.app.printWhite

class ThreadRecall(private val assistantsApi: AssistantsApi, private val userProperties: UserProperties) :
    Command() {
    override val requiresApiKey: Boolean = true
    override suspend fun execute() {
        val threadId = userProperties.getThreadId()
        if (threadId.isEmpty()) {
            println("You need to create a thread first. Use /thread-new")
            return
        }
        val messages = assistantsApi.messages.listMessages(threadId)
        messages.data.forEach {
            if (it.role == "user") {
                print("${green("ask ➜")} ")
                println(it.content)
            } else {
                printWhite()
                println(it.content)
            }
        }
    }
}