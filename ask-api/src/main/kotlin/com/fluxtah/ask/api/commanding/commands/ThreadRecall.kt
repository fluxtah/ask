/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.api.commanding.commands

import com.fluxtah.ask.api.ansi.green
import com.fluxtah.ask.api.clients.openai.assistants.AssistantsApi
import com.fluxtah.ask.api.markdown.AnsiMarkdownRenderer
import com.fluxtah.ask.api.markdown.MarkdownParser
import com.fluxtah.ask.api.printers.AskResponsePrinter
import com.fluxtah.ask.api.store.user.UserProperties

class ThreadRecall(
    private val assistantsApi: AssistantsApi,
    private val userProperties: UserProperties,
    private val printer: AskResponsePrinter
) : Command() {
    override val requiresApiKey: Boolean = true
    override suspend fun execute(args: List<String>) {
        val threadId = userProperties.getThreadId()
        if (threadId.isEmpty()) {
            printer.printMessage("You need to create a thread first. Use /thread-new")
            return
        }
        val messages = assistantsApi.messages.listMessages(threadId)
        printer
            .begin()
            .println()
            .println("-- Thread Recall $threadId --")
            .println()
            .apply {
                messages.data.reversed().forEach { message ->
                    if (message.role == "user") {
                        print("${green("ask ➜")} ")
                        message.content.forEach { content ->
                            println(content.text.value)
                        }
                    } else {
                        println("\u001B[0m")
                        message.content.forEach { content ->
                            val markdownParser = MarkdownParser(content.text.value)
                            val ansiMarkdown = AnsiMarkdownRenderer().render(markdownParser.parse())
                            println(ansiMarkdown)
                        }
                        println()
                    }
                    println()
                }
            }
            .end()
    }
}