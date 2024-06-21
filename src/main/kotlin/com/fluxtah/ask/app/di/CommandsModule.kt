package com.fluxtah.ask.app.di

import com.fluxtah.ask.app.commanding.commands.Clear
import com.fluxtah.ask.app.commanding.commands.ClearModel
import com.fluxtah.ask.app.commanding.commands.DeleteThread
import com.fluxtah.ask.app.commanding.commands.EnableTalkCommand
import com.fluxtah.ask.app.commanding.commands.Exit
import com.fluxtah.ask.app.commanding.commands.GetAssistant
import com.fluxtah.ask.app.commanding.commands.GetThread
import com.fluxtah.ask.app.commanding.commands.Help
import com.fluxtah.ask.app.commanding.commands.InstallAssistant
import com.fluxtah.ask.app.commanding.commands.ListAssistants
import com.fluxtah.ask.app.commanding.commands.ListMessages
import com.fluxtah.ask.app.commanding.commands.ListRunSteps
import com.fluxtah.ask.app.commanding.commands.ListRuns
import com.fluxtah.ask.app.commanding.commands.ListThreads
import com.fluxtah.ask.app.commanding.commands.MaxCompletionTokens
import com.fluxtah.ask.app.commanding.commands.MaxPromptTokens
import com.fluxtah.ask.app.commanding.commands.PlayTts
import com.fluxtah.ask.app.commanding.commands.RecordVoice
import com.fluxtah.ask.app.commanding.commands.RecoverRun
import com.fluxtah.ask.app.commanding.commands.ReinstallAssistant
import com.fluxtah.ask.app.commanding.commands.SetLogLevel
import com.fluxtah.ask.app.commanding.commands.SetModel
import com.fluxtah.ask.app.commanding.commands.SetOpenAiApiKey
import com.fluxtah.ask.app.commanding.commands.ShellExec
import com.fluxtah.ask.app.commanding.commands.ShowHttpLog
import com.fluxtah.ask.app.commanding.commands.SkipTts
import com.fluxtah.ask.app.commanding.commands.SwitchThread
import com.fluxtah.ask.app.commanding.commands.ThreadNew
import com.fluxtah.ask.app.commanding.commands.ThreadRecall
import com.fluxtah.ask.app.commanding.commands.ThreadRename
import com.fluxtah.ask.app.commanding.commands.TruncateLastMessages
import com.fluxtah.ask.app.commanding.commands.UnInstallAssistant
import com.fluxtah.ask.app.commanding.commands.VoiceAutoSendCommand
import com.fluxtah.ask.app.commanding.commands.WhichAssistant
import com.fluxtah.ask.app.commanding.commands.WhichModel
import com.fluxtah.ask.app.commanding.commands.WhichThread
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val commandsModule = module {
    factoryOf(::MaxCompletionTokens)
    factoryOf(::MaxPromptTokens)
    factoryOf(::Help)
    factoryOf(::Exit)
    factoryOf(::Clear)
    factoryOf(::TruncateLastMessages)
    factoryOf(::InstallAssistant)
    factoryOf(::UnInstallAssistant)
    factoryOf(::ListAssistants)
    factoryOf(::WhichAssistant)
    factoryOf(::GetAssistant)
    factoryOf(::SetModel)
    factoryOf(::ClearModel)
    factoryOf(::WhichModel)
    factoryOf(::ThreadNew)
    factoryOf(::WhichThread)
    factoryOf(::GetThread)
    factoryOf(::DeleteThread)
    factoryOf(::ListThreads)
    factoryOf(::SwitchThread)
    factoryOf(::ThreadRename)
    factoryOf(::ThreadRecall)
    factoryOf(::ListMessages)
    factoryOf(::ListRuns)
    factoryOf(::ListRunSteps)
    factoryOf(::RecoverRun)
    factoryOf(::ShowHttpLog)
    factoryOf(::SetOpenAiApiKey)
    factoryOf(::SetLogLevel)
    factoryOf(::ShellExec)
    factoryOf(::ReinstallAssistant)
    factoryOf(::VoiceAutoSendCommand)
    factoryOf(::RecordVoice)
    factoryOf(::SkipTts)
    factoryOf(::PlayTts)
    factoryOf(::EnableTalkCommand)
}
