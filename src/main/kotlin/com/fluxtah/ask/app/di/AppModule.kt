/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */
package com.fluxtah.ask.app.di

import com.fluxtah.ask.api.printers.AskConsoleResponsePrinter
import com.fluxtah.ask.api.printers.AskResponsePrinter
import com.fluxtah.ask.app.AskCommandCompleter
import com.fluxtah.ask.app.ConsoleApplication
import com.fluxtah.ask.app.ConsoleOutputRenderer
import com.fluxtah.ask.app.WorkingSpinner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.jline.reader.LineReaderBuilder
import org.jline.terminal.TerminalBuilder
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    single { CoroutineScope(Dispatchers.Default) }
    singleOf<AskResponsePrinter>(::AskConsoleResponsePrinter)
    singleOf(::ConsoleOutputRenderer)
    singleOf(::AskCommandCompleter)
    singleOf(::WorkingSpinner)

    singleOf(::ConsoleApplication)
    single {
        TerminalBuilder.builder()
            .system(true)
            .build()
    }
    single {
        LineReaderBuilder.builder()
            .terminal(get())
            .completer(get<AskCommandCompleter>())
            .build()
    }
}

