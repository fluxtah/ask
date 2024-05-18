/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.app.commanding.commands

import com.fluxtah.ask.app.UserProperties

class WhichModel(
    private val userProperties: UserProperties
) : Command() {
    override val requiresApiKey: Boolean = false
    override suspend fun execute() {
        val modelId = userProperties.getModel()
        if (modelId.isEmpty()) {
            println("No model set, all targeted assistants will use their default model")
            return
        }
        println("Model set to $modelId, all targeted assistants will use this model until you /model-clear")
    }
}