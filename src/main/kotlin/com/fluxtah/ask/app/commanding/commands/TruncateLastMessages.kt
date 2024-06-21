/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.app.commanding.commands

import com.fluxtah.ask.api.printers.AskResponsePrinter
import com.fluxtah.ask.api.store.user.UserProperties

class TruncateLastMessages(
    private val userProperties: UserProperties,
    private val printer: AskResponsePrinter
) : Command() {
    override val requiresApiKey: Boolean = false

    override suspend fun execute(args: List<String>) {
        val number = args.firstOrNull()?.toIntOrNull()
        if (number == null) {
            val currentValue = userProperties.getTruncateLastMessages()
            printer.println("Current truncate last messages value: $currentValue. Usage: /truncate-last-messages <number>")
            return
        }

        if (number < 0) {
            printer.println("Invalid number. Please provide a number between 0 and a positive integer.")
        } else {
            userProperties.setTruncateLastMessages(number)
            userProperties.save()
            printer.println("Set the truncate last messages value to: $number")
        }
    }
}
