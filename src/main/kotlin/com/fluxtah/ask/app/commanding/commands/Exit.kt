/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.app.commanding.commands

import com.fluxtah.ask.api.printers.AskResponsePrinter
import kotlin.system.exitProcess

class Exit(private val printer: AskResponsePrinter) : Command() {
    override val requiresApiKey: Boolean = false
    override suspend fun execute(args: List<String>) {
        printer.println("Exiting the application...")
        exitProcess(0)
    }
}
