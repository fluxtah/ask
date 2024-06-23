/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.api.commanding.commands

import com.fluxtah.ask.api.printers.AskResponsePrinter
import com.fluxtah.ask.api.commanding.CommandFactory
import kotlinx.coroutines.delay

class Help(
    private val commandFactory: CommandFactory,
    private val printer: AskResponsePrinter
) : Command() {
    override val requiresApiKey: Boolean = false
    override suspend fun execute(args: List<String>) {
        printer
            .begin()
            .println(String.format("%-20s %-30s", "Command", "Description"))
            .println("--------------------------------------------------------------------------------")
            .apply {
                commandFactory.getCommandsSortedByName().forEach {
                    println(String.format("%-20s %-30s", it.name, it.description))
                }

            }
            .end()
    }
}

