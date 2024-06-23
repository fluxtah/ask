/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.api.commanding.commands

import com.fluxtah.ask.api.clients.openai.assistants.HTTP_LOG
import com.fluxtah.ask.api.printers.AskResponsePrinter

class ShowHttpLog(private val printer: AskResponsePrinter) : Command() {
    override val requiresApiKey: Boolean = false
    override suspend fun execute(args: List<String>) {
        HTTP_LOG.forEach {
            printer.printMessage(it)
        }
    }
}