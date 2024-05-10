package com.fluxtah.ask.api.printers

interface AskResponsePrinter {
    fun println(message: String? = null)
    fun print(message: String)
}