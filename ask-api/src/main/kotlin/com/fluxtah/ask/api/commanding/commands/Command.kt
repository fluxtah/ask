/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.api.commanding.commands

abstract class Command {
    abstract val requiresApiKey: Boolean
    abstract suspend fun execute(args: List<String>)
}
