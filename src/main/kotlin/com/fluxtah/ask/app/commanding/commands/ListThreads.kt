/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.app.commanding.commands

import com.fluxtah.ask.app.UserProperties
import com.fluxtah.ask.api.repository.ThreadRepository

class ListThreads(private val userProperties: UserProperties, private val threadRepository: ThreadRepository) :
    Command() {
    override val requiresApiKey: Boolean = true
    override suspend fun execute() {
        // TODO currently its not possible to list threads though the API should be up soon
        //println(assistantsApi.threads.listThreads())

        val threads = threadRepository.listThreads()
        println()
        println(String.format("%-36s %-30s", "Thread", "Title"))
        println("--------------------------------------------------------------------------------")
        if (threads.isEmpty()) {
            println("No threads found, type /thread-new to create a new thread")
        } else {
            threads.forEach {
                val title = it.title.ifEmpty { "<unnamed>" }
                if (userProperties.getThreadId() == it.threadId) {
                    println(String.format("%-36s %-30s", it.threadId, "$title (Active)"))
                } else {
                    println(String.format("%-36s %-30s", it.threadId, title))
                }
            }
        }
    }
}