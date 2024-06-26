/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.api.commanding.commands

import com.fluxtah.ask.api.printers.AskResponsePrinter
import com.fluxtah.ask.api.store.user.UserProperties

class WhichModel(
    private val userProperties: UserProperties,
    private val printer: AskResponsePrinter
) : Command() {
    override val requiresApiKey: Boolean = false
    override suspend fun execute(args: List<String>) {
        val modelId = userProperties.getModel()
        if (modelId.isEmpty()) {
            printer.printMessage("No model set, all targeted assistants will use their default model")
            return
        }
        printer.printMessage("Model set to $modelId, all targeted assistants will use this model until you /model-clear")
    }
}