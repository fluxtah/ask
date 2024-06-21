package com.fluxtah.ask.api.di

import AudioPlayer
import com.fluxtah.ask.api.AssistantRunManager
import com.fluxtah.ask.api.AssistantRunner
import com.fluxtah.ask.api.InputHandler
import com.fluxtah.ask.api.assistants.AssistantInstallRepository
import com.fluxtah.ask.api.assistants.AssistantRegistry
import com.fluxtah.ask.api.audio.AudioRecorder
import com.fluxtah.ask.api.audio.TextToSpeechPlayer
import com.fluxtah.ask.api.clients.openai.assistants.AssistantsApi
import com.fluxtah.ask.api.clients.openai.audio.AudioApi
import com.fluxtah.ask.api.repository.ThreadRepository
import com.fluxtah.ask.api.store.PropertyStore
import com.fluxtah.ask.api.store.user.UserProperties
import com.fluxtah.ask.api.tools.fn.FunctionInvoker
import com.fluxtah.askpluginsdk.logging.AskLogger
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val askApiModule = module {
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
    singleOf(::ThreadRepository)
    singleOf(::FunctionInvoker)
    singleOf(::AssistantRunner)
    singleOf(::AssistantRunManager)
    singleOf(::AudioPlayer)
    singleOf(::TextToSpeechPlayer)
    singleOf(::AudioRecorder)
    singleOf(::InputHandler)


}