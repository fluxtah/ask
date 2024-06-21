/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.api.commanding.commands

import com.fluxtah.ask.api.printers.AskResponsePrinter
import com.fluxtah.ask.api.store.user.UserProperties

class SetModel(
    private val userProperties: UserProperties,
    private val printer: AskResponsePrinter
) : Command() {
    override val requiresApiKey: Boolean = false
    override suspend fun execute(args: List<String>) {
        if (args.size != 1) {
            printer.println("Invalid number of arguments for /model, expected a model ID following the command")
            return
        }

        val modelId = args.first()
        userProperties.setModel(modelId)
        userProperties.save()
        printer.println("Model set to $modelId, all targeted assistants will use this model until you /model-clear")
    }
}