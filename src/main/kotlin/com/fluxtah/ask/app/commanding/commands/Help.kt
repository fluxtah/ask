/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.app.commanding.commands

import com.fluxtah.ask.api.printers.AskResponsePrinter
import com.fluxtah.ask.app.commanding.CommandFactory

class Help(
    private val commandFactory: CommandFactory, private val printer: AskResponsePrinter
) : Command() {
    override val requiresApiKey: Boolean = false
    override suspend fun execute() {
        printer.println(String.format("%-20s %-30s", "Command", "Description"))
        printer.println("--------------------------------------------------------------------------------")
        commandFactory.getCommands().forEach {
            printer.println(String.format("%-20s %-30s", it.name, it.description))
        }
    }
}

