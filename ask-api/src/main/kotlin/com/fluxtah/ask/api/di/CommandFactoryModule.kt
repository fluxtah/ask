package com.fluxtah.ask.api.di

import com.fluxtah.ask.api.commanding.CommandFactory
import com.fluxtah.ask.api.commanding.commands.Clear
import com.fluxtah.ask.api.commanding.commands.ClearModel
import com.fluxtah.ask.api.commanding.commands.DeleteThread
import com.fluxtah.ask.api.commanding.commands.EnableTalkCommand
import com.fluxtah.ask.api.commanding.commands.Exit
import com.fluxtah.ask.api.commanding.commands.GetAssistant
import com.fluxtah.ask.api.commanding.commands.GetThread
import com.fluxtah.ask.api.commanding.commands.Help
import com.fluxtah.ask.api.commanding.commands.InstallAssistant
import com.fluxtah.ask.api.commanding.commands.ListAssistants
import com.fluxtah.ask.api.commanding.commands.ListMessages
import com.fluxtah.ask.api.commanding.commands.ListRunSteps
import com.fluxtah.ask.api.commanding.commands.ListRuns
import com.fluxtah.ask.api.commanding.commands.ListThreads
import com.fluxtah.ask.api.commanding.commands.MaxCompletionTokens
import com.fluxtah.ask.api.commanding.commands.MaxPromptTokens
import com.fluxtah.ask.api.commanding.commands.PlayTts
import com.fluxtah.ask.api.commanding.commands.RecordVoice
import com.fluxtah.ask.api.commanding.commands.RecoverRun
import com.fluxtah.ask.api.commanding.commands.ReinstallAssistant
import com.fluxtah.ask.api.commanding.commands.SetLogLevel
import com.fluxtah.ask.api.commanding.commands.SetModel
import com.fluxtah.ask.api.commanding.commands.SetOpenAiApiKey
import com.fluxtah.ask.api.commanding.commands.ShellExec
import com.fluxtah.ask.api.commanding.commands.ShowHttpLog
import com.fluxtah.ask.api.commanding.commands.SkipTts
import com.fluxtah.ask.api.commanding.commands.SwitchThread
import com.fluxtah.ask.api.commanding.commands.ThreadNew
import com.fluxtah.ask.api.commanding.commands.ThreadRecall
import com.fluxtah.ask.api.commanding.commands.ThreadRename
import com.fluxtah.ask.api.commanding.commands.TruncateLastMessages
import com.fluxtah.ask.api.commanding.commands.UnInstallAssistant
import com.fluxtah.ask.api.commanding.commands.VoiceAutoSendCommand
import com.fluxtah.ask.api.commanding.commands.WhichAssistant
import com.fluxtah.ask.api.commanding.commands.WhichModel
import com.fluxtah.ask.api.commanding.commands.WhichThread
import org.koin.dsl.module

val commandFactoryModule = module {
    single {
        CommandFactory(get(), get()).apply {
            registerCommand<MaxCompletionTokens>(
                name = "max-completion-tokens",
                description = "<number> - Set the max completion tokens value"
            )
            registerCommand<MaxPromptTokens>(
                name = "max-prompt-tokens",
                description = "<number> - Set the max prompt tokens value"
            )
            registerCommand<Help>(
                name = "help",
                description = "Show this help"
            )
            registerCommand<Exit>(
                name = "exit",
                description = "Exits ask"
            )
            registerCommand<Clear>(
                name = "clear",
                description = "Clears the screen"
            )
            registerCommand<TruncateLastMessages>(
                name = "truncate-last-messages",
                description = "<number> - Set or get the truncate last messages value"
            )
            registerCommand<InstallAssistant>(
                name = "assistant-install",
                description = "<assistant-id> Installs an assistant"
            )
            registerCommand<UnInstallAssistant>(
                name = "assistant-uninstall",
                description = "<assistant-id> Uninstalls an assistant"
            )
            registerCommand<ListAssistants>(
                name = "assistant-list",
                description = "Displays all available assistants",
            )
            registerCommand<WhichAssistant>(
                name = "assistant-which",
                description = "Displays the current assistant thread"
            )
            registerCommand<GetAssistant>(
                name = "assistant-info",
                description = "<assistant-id> Displays info for the assistant"
            )
            registerCommand<SetModel>(
                name = "model",
                description = "<model-id> Set model override affecting all assistants (gpt-3.5-turbo-16k, gpt-4-turbo, etc.)"
            )
            registerCommand<ClearModel>(
                name = "model-clear",
                description = "Clears the current model override"
            )
            registerCommand<WhichModel>(
                name = "model-which",
                description = "Displays the current model override"
            )
            registerCommand<ThreadNew>(
                name = "thread-new",
                description = "Creates a new assistant thread"
            )
            registerCommand<WhichThread>(
                name = "thread-which",
                description = "Displays the current assistant thread"
            )
            registerCommand<GetThread>(
                name = "thread-info",
                description = "<thread-id> - Displays the assistant thread",
            )
            registerCommand<DeleteThread>(
                name = "thread-delete",
                description = "<thread-id> - Delete the thread by the given id"
            )

            registerCommand<ListThreads>(
                name = "thread-list",
                description = "Lists all assistant threads"
            )
            registerCommand<SwitchThread>(
                name = "thread-switch",
                description = "<thread-id> - Switches to the given thread"
            )
            registerCommand<ThreadRename>(
                name = "thread-rename",
                description = "<thread-id> <new-title> - Renames the given thread"
            )
            registerCommand<ThreadRecall>(
                name = "thread-recall",
                description = "Recalls the current assistant thread messages (prints out message history)"
            )
            registerCommand<ListMessages>(
                name = "message-list",
                description = "Lists all messages in the current assistant thread"
            )
            registerCommand<ListRuns>(
                name = "run-list",
                description = "Lists all runs in the current assistant thread"
            )
            registerCommand<ListRunSteps>(
                name = "run-step-list",
                description = "Lists all run steps in the current assistant thread"
            )
            registerCommand<RecoverRun>(
                name = "run-recover",
                description = "Recovers the last run in the current assistant thread"
            )
            registerCommand<ShowHttpLog>(
                name = "http-log",
                description = "Displays the last 10 HTTP requests"
            )
            registerCommand<SetOpenAiApiKey>(
                name = "set-key",
                description = "<api-key> - Set your openai api key"
            )

            registerCommand<SetLogLevel>(
                name = "log-level",
                description = "<level> Set the log level (ERROR, DEBUG, INFO, OFF)"
            )

            registerCommand<ShellExec>(
                name = "exec",
                description = "<command> - Executes a shell command for convenience"
            )
            registerCommand<ReinstallAssistant>(
                name = "assistant-reinstall",
                description = "<assistant-id> Reinstall an assistant"
            )

            registerCommand<VoiceAutoSendCommand>(
                name = "voice-auto-send",
                description = "Toggles auto-send mode for voice commands"
            )
            registerCommand<RecordVoice>(
                name = "r",
                description = "Start recording audio"
            )
            registerCommand<SkipTts>(
                name = "s",
                description = "Skip the current text-to-speech segment"
            )
            registerCommand<PlayTts>(
                name = "p",
                description = "Play the current text-to-speech segment"
            )
            registerCommand<EnableTalkCommand>(
                name = "talk",
                description = "Stop the current text-to-speech segment"
            )
        }
    }
}