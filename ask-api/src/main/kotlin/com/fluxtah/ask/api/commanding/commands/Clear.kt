/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.api.commanding.commands

import com.fluxtah.ask.api.printers.AskResponsePrinter

class Clear(private val printer: AskResponsePrinter) : Command() {
    override suspend fun execute(args: List<String>) {
        printer.printMessage("\u001b[H\u001b[2J")
    }

    override val requiresApiKey: Boolean = false
}