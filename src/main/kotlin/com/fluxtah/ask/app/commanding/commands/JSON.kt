/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.app.commanding.commands

import kotlinx.serialization.json.Json

internal val JSON = Json {
    isLenient = true
    prettyPrint = true
}