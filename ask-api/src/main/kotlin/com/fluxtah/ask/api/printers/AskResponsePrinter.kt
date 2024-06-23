/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.api.printers

interface AskResponsePrinter {
    fun begin(): PrinterContext
    fun printMessage(message: String) {
        begin().println(message).end()
    }
}

interface PrinterContext {
    fun println(line: String? = null): PrinterContext
    fun print(text: String): PrinterContext
    fun end()
}