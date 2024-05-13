/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.api.printers

class AskConsoleResponsePrinter : AskResponsePrinter {
    override fun println(message: String?) {
        kotlin.io.println(message ?: "")
    }

    override fun print(message: String) {
        kotlin.io.print(message)
    }

    override fun flush() {
        System.out.flush()
    }
}