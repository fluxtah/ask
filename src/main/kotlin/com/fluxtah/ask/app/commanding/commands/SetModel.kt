package com.fluxtah.ask.app.commanding.commands

import com.fluxtah.ask.app.UserProperties

class SetModel(
    private val userProperties: UserProperties,
    private val modelId: String
) : Command() {
    override val requiresApiKey: Boolean = false
    override suspend fun execute() {
        userProperties.setModel(modelId)
        userProperties.save()
        println("Model set to $modelId, all targeted assistants will use this model until you /model-clear")
    }
}