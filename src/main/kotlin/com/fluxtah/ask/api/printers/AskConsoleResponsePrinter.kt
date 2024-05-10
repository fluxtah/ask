package com.fluxtah.ask.api.printers

class AskConsoleResponsePrinter : AskResponsePrinter {
    override fun println(message: String?) {
        kotlin.io.println(message ?: "")
    }

    override fun print(message: String) {
        kotlin.io.print(message)
    }
}