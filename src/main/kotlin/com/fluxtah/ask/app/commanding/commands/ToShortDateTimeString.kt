package com.fluxtah.ask.app.commanding.commands

import java.util.*

fun Long.toShortDateTimeString(): String {
    val dt = Date(this * 1000)
    return String.format("%tF %<tT", dt)
}