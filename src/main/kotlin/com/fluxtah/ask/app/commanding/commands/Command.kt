/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.app.commanding.commands

sealed class Command {
    abstract suspend fun execute()
    abstract val requiresApiKey: Boolean
}


