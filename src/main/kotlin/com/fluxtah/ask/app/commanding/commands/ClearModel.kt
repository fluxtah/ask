package com.fluxtah.ask.app.commanding.commands

import com.fluxtah.ask.app.UserProperties

class ClearModel(
    private val userProperties: UserProperties
) : Command() {
    override val requiresApiKey: Boolean = false
    override suspend fun execute() {
        userProperties.setModel("")
        userProperties.save()
        println("Model cleared, all targeted assistants will use their default model")
    }
}