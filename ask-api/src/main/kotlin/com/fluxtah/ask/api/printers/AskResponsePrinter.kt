/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.api.printers

interface AskResponsePrinter {
    fun println(message: String? = null)
    fun print(message: String)
    fun flush()
}