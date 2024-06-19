/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.app.commanding.commands

import java.util.*

fun Long.toShortDateTimeString(): String {
    val dt = Date(this * 1000)
    return String.format("%tF %<tT", dt)
}