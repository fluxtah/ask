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
    private val printer: AskResponsePrinter,
    private val number: Int? = null
) : Command() {
    override val requiresApiKey: Boolean = false

    override suspend fun execute() {
        if (number == null) {
            val currentValue = userProperties.getTruncateLastMessages()
            printer.println("Current truncate last messages value: $currentValue. To set a new value, use /truncate-last-messages <number>")
        } else if (number < 0) {
            printer.println("Invalid number. Please provide a number between 0 and a positive integer.")
        } else {
            userProperties.setTruncateLastMessages(number)
            userProperties.save()
            printer.println("Set the truncate last messages value to: $number")
        }
    }
}
