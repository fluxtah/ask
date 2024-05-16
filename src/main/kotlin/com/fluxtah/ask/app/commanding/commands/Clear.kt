/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.app.commanding.commands

class Clear : Command() {
    companion object {
        const val NAME = "clear"
    }
    override suspend fun execute() {
        println("\u001b[H\u001b[2J")
    }

    override val requiresApiKey: Boolean = false
}