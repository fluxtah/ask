/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.api.commanding.commands

import com.fluxtah.ask.api.printers.AskResponsePrinter
import com.fluxtah.ask.api.store.user.UserProperties

class ClearModel(
    private val userProperties: UserProperties,
    private val printer: AskResponsePrinter
) : Command() {
    override val requiresApiKey: Boolean = false
    override suspend fun execute(args: List<String>) {
        userProperties.setModel("")
        userProperties.save()
        printer.printMessage("Model cleared, all targeted assistants will use their default model")
    }
}
