/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.app.commanding.commands

import kotlin.system.exitProcess

data object Exit : Command() {
    override val requiresApiKey: Boolean = false
    override suspend fun execute() {
        println("Exiting the application...")
        exitProcess(0)
    }
}
