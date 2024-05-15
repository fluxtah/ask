package com.fluxtah.ask.app.commanding.commands

import com.fluxtah.ask.api.UserProperties
import com.fluxtah.ask.api.ansi.blue
import com.fluxtah.ask.api.ansi.green
import com.fluxtah.ask.api.ansi.white
import com.fluxtah.ask.api.clients.openai.assistants.AssistantsApi
import com.fluxtah.ask.api.parser.MarkdownParser
import com.fluxtah.ask.api.parser.Token

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
        println()
        println("-- Thread Recall $threadId --")
        println()
        messages.data.reversed().forEach { message ->
            if (message.role == "user") {
                print("${green("ask ➜")} ")
                message.content.forEach { content ->
                    println(content.text.value)
                }
            } else {
                println(white())
                message.content.forEach { content ->
                    val markdownParser = MarkdownParser(content.text.value)
                    markdownParser.parse().forEach { token ->
                        when (token) {
                            is Token.CodeBlock -> {
                                println()
                                println()
                                println(blue(token.content.trim()))
                                println()
                                println(white())
                            }

                            is Token.Text -> {
                                print(token.content.trim())
                            }
                        }
                    }
                }
                println()
            }
            println()
        }
    }
}