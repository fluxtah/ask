package com.fluxtah.ask.app.commanding.commands

import com.fluxtah.ask.app.UserProperties

class TruncateLastMessages(
    private val userProperties: UserProperties,
    private val number: Int? = null
) : Command() {
    override val requiresApiKey: Boolean = false

    override suspend fun execute() {
        if (number == null) {
            val currentValue = userProperties.getTruncateLastMessages()
            println("Current truncate last messages value: $currentValue. To set a new value, use /truncate-last-messages <number>")
        } else if (number < 0) {
            println("Invalid number. Please provide a number between 0 and a positive integer.")
        } else {
            userProperties.setTruncateLastMessages(number)
            userProperties.save()
            println("Set the truncate last messages value to: $number")
        }
    }
}
