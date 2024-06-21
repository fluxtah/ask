/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */
package com.fluxtah.ask.app.di

import AudioPlayer
import com.fluxtah.ask.api.AssistantRunManager
import com.fluxtah.ask.api.AssistantRunner
import com.fluxtah.ask.api.assistants.AssistantInstallRepository
import com.fluxtah.ask.api.assistants.AssistantRegistry
import com.fluxtah.ask.api.audio.AudioRecorder
import com.fluxtah.ask.api.audio.TextToSpeechPlayer
import com.fluxtah.ask.api.clients.openai.assistants.AssistantsApi
import com.fluxtah.ask.api.clients.openai.audio.AudioApi
import com.fluxtah.ask.api.printers.AskConsoleResponsePrinter
import com.fluxtah.ask.api.printers.AskResponsePrinter
import com.fluxtah.ask.api.repository.ThreadRepository
import com.fluxtah.ask.api.store.PropertyStore
import com.fluxtah.ask.api.store.user.UserProperties
import com.fluxtah.ask.api.tools.fn.FunctionInvoker
import com.fluxtah.ask.app.AskCommandCompleter
import com.fluxtah.ask.app.ConsoleApplication
import com.fluxtah.ask.app.ConsoleOutputRenderer
import com.fluxtah.ask.app.InputHandler
import com.fluxtah.ask.app.WorkingSpinner
import com.fluxtah.askpluginsdk.logging.AskLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.jline.reader.LineReaderBuilder
import org.jline.terminal.TerminalBuilder
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    single { CoroutineScope(Dispatchers.Default) }
    single { UserProperties(PropertyStore("user.properties")) }
    singleOf(::AskLogger)
    single {
        AssistantsApi(
            apiKeyProvider = { get<UserProperties>().getOpenaiApiKey() }
        )
    }
    single {
        AudioApi(
            apiKeyProvider = { get<UserProperties>().getOpenaiApiKey() }
        )
    }
    singleOf(::AssistantRegistry)
    singleOf(::AssistantInstallRepository)
    singleOf<AskResponsePrinter>(::AskConsoleResponsePrinter)
    singleOf(::ThreadRepository)
    singleOf(::FunctionInvoker)
    singleOf(::AssistantRunner)
    singleOf(::AssistantRunManager)
    singleOf(::AudioPlayer)
    singleOf(::TextToSpeechPlayer)
    singleOf(::AudioRecorder)
    singleOf(::InputHandler)
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

