/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.api.printers

class ConsolePrinterContext(val printer: AskConsoleResponsePrinter) : PrinterContext {
    override fun println(line: String?): PrinterContext {
        kotlin.io.println(line ?: "")
        return this
    }

    override fun print(text: String): PrinterContext {
        kotlin.io.print(text)
        return this
    }

    override fun end() {
        printer.currentContext = null
    }
}

class AskConsoleResponsePrinter : AskResponsePrinter {
    var currentContext: ConsolePrinterContext? = null

    override fun begin(): PrinterContext {
        if (currentContext != null) {
            throw IllegalStateException("Printer context already in use, call end() before starting a new context")
        }
        return ConsolePrinterContext(this)
    }
}