/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */
package com.fluxtah.ask.app

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import com.fluxtah.ask.api.AssistantRunManager
import com.fluxtah.ask.api.InputHandler
import com.fluxtah.ask.api.RunManagerStatus
import com.fluxtah.ask.api.ansi.green
import com.fluxtah.ask.api.ansi.red
import com.fluxtah.ask.api.assistants.AssistantRegistry
import com.fluxtah.ask.api.audio.AudioRecorder
import com.fluxtah.ask.api.audio.TextToSpeechPlayer
import com.fluxtah.ask.api.clients.openai.audio.AudioApi
import com.fluxtah.ask.api.clients.openai.audio.model.CreateTranscriptionRequest
import com.fluxtah.ask.api.plugins.AskPluginLoader
import com.fluxtah.ask.api.printers.AskResponsePrinter
import com.fluxtah.ask.api.store.user.UserProperties
import com.fluxtah.askpluginsdk.logging.AskLogger
import com.fluxtah.askpluginsdk.logging.LogLevel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jline.reader.LineReader
import java.io.File
import kotlin.system.exitProcess

class ConsoleApplication(
    private val logger: AskLogger,
    private val coroutineScope: CoroutineScope,
    private val userProperties: UserProperties,
    private val audioApi: AudioApi,
    private val assistantRegistry: AssistantRegistry,
    private val responsePrinter: AskResponsePrinter,
    assistantRunManager: AssistantRunManager,
    private val tts: TextToSpeechPlayer,
    private val audioRecorder: AudioRecorder,
    private val inputHandler: InputHandler,
    private val consoleOutputRenderer: ConsoleOutputRenderer,
    private val lineReader: LineReader
) {
    private var transcribedText: String = ""

    init {
        val exposedLogger = org.jetbrains.exposed.sql.exposedLogger as Logger
        exposedLogger.level = Level.OFF

        userProperties.load()

        AskPluginLoader(logger).loadPlugins().forEach {
            assistantRegistry.register(it)
        }

        tts.enabled = userProperties.getTalkEnabled()

        assistantRunManager.onStatusChanged = ::onAssistantStatusChanged
    }

    fun runOneShotCommand(command: String) {
        inputHandler.handleInput(command)
        exitProcess(0)
    }

    fun run() {
        consoleOutputRenderer.renderWelcomeMessage()
        initLogLevelAndPrint()

        Runtime.getRuntime().addShutdownHook(Thread {
            coroutineScope.launch {
                audioRecorder.stop()
            }
        })

        while (true) {
            println()

            try {
                val input = commandPromptReadLine()
                transcribedText = ""

                if (audioRecorder.isRecording()) {
                    handleRecordingComplete()
                    continue
                }

                inputHandler.handleInput(input)
            } catch (e: Exception) {
                logger.log(LogLevel.ERROR, "Error: ${e.message}")
            }
        }
    }

    private fun handleRecordingComplete() {
        endAudioRecording()
        transcribeAudioRecording()
        if (userProperties.getAutoSendVoice()) {
            println()
            responsePrinter.println("${green(promptText())} $transcribedText")
            inputHandler.handleInput(transcribedText)
            transcribedText = ""
        }
    }

    fun debugPlugin(pluginFile: File) {
        assistantRegistry.register(AskPluginLoader(logger).loadPlugin(pluginFile))

        run()
    }

    private fun onAssistantStatusChanged(status: RunManagerStatus) {
        when (status) {
            is RunManagerStatus.Response -> {
                consoleOutputRenderer.renderAssistantResponse(status.response)
                tts.queue(status.response)
                tts.playNext()
            }

            is RunManagerStatus.ToolCall -> {
                consoleOutputRenderer.renderAssistantToolCall(status.details)
            }

            is RunManagerStatus.MessageCreated -> {
                consoleOutputRenderer.renderAssistantMessage(status.message)
                tts.queue(status.message.content.firstOrNull()?.text?.value ?: "")
                tts.playNext()
            }

            is RunManagerStatus.Error -> {
                consoleOutputRenderer.renderAssistantError(status)
                tts.queue(status.message)
                tts.playNext()
            }

            is RunManagerStatus.RunStatusChanged -> {
                consoleOutputRenderer.renderAssistantRunStatusChanged(status.runStatus)
            }

            RunManagerStatus.BeforeBeginRun -> {
                responsePrinter.println()
                tts.stop()
                tts.clear()
            }
        }
    }

    private fun transcribeAudioRecording() {
        runBlocking {
            val response = audioApi.createTranscription(
                CreateTranscriptionRequest(audioRecorder.getAudioFile())
            )

            transcribedText = response.text
        }
    }

    private fun endAudioRecording() {
        coroutineScope.launch {
            audioRecorder.stop()
        }
        clearLinesAndSleep()
    }

    private fun clearLinesAndSleep(backTimes: Int = 2, waitTime: Long = 200) {
        for (i in 1..backTimes) {
            responsePrinter.print("\u001b[1A\u001b[2K")
        }
        Thread.sleep(waitTime)
    }

    private fun initLogLevelAndPrint() {
        val logLevel = userProperties.getLogLevel()
        if (logLevel != LogLevel.OFF) {
            logger.log(LogLevel.DEBUG, "Log level: $logLevel")
        }

        logger.setLogLevel(logLevel)
    }

    private fun commandPromptReadLine(): String {
        val prompt = promptText()

        return when {
            audioRecorder.isRecording() -> {
                lineReader.readLine(red("[Recording \uD83C\uDFA4 - ENTER to stop]"))
            }

            else -> {
                if (transcribedText.isNotEmpty()) {
                    lineReader.readLine("${green(prompt)} ", null, transcribedText)
                } else {
                    lineReader.readLine(green(prompt) + " ")
                }
            }
        }
    }

    private fun promptText(): String {
        val prompt = if (userProperties.getAssistantId().isEmpty()) {
            "ask ➜"
        } else {
            "ask@${userProperties.getAssistantId()} ➜"
        }
        return prompt
    }
}
