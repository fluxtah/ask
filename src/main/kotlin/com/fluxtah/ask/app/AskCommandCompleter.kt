package com.fluxtah.ask.app

import com.fluxtah.ask.api.assistants.AssistantRegistry
import org.jetbrains.kotlin.util.prefixIfNot
import org.jline.reader.Candidate
import org.jline.reader.Completer
import org.jline.reader.LineReader
import org.jline.reader.ParsedLine

class AskCommandCompleter(val assistantRegistry: AssistantRegistry) : Completer {
    private val commands = listOf(
        "/exit", "/assistant-list", "/assistant-install", "/assistant-uninstall",
        "/assistant-which", "/assistant-info", "/model", "/model-clear", "/model-which",
        "/thread", "/thread-which", "/thread-list", "/thread-info", "/message-list",
        "/run-list", "/run-step-list", "/http-log", "/set-key", "/log-level"
    )

    private val models = listOf("gpt-3.5-turbo-16k", "gpt-4-turbo")

    private val logLevels = listOf("ERROR", "DEBUG", "INFO", "OFF")

    override fun complete(reader: LineReader?, line: ParsedLine?, candidates: MutableList<Candidate>?) {
        if (line == null || candidates == null) return

        val words = line.words()
        val wordIndex = line.wordIndex()
        val currentWord = if (wordIndex < words.size) words[wordIndex] else ""

        when {
            currentWord.startsWith("@") -> {
                assistantRegistry.getAssistants().map { it.id.prefixIfNot("@") }.filter { it.startsWith(currentWord) }
                    .forEach { candidates.add(Candidate(it)) }
            }

            currentWord.startsWith("/assistant-info") ||
                    currentWord.startsWith("/assistant-install") ||
                    currentWord.startsWith("/assistant-uninstall") -> {
                assistantRegistry.getAssistants().forEach { candidates.add(Candidate(it.name)) }
            }

            currentWord.startsWith("/model") && wordIndex == 1 -> {
                models.forEach { candidates.add(Candidate(it)) }
            }

            currentWord.startsWith("/log-level") && wordIndex == 1 -> {
                logLevels.forEach { candidates.add(Candidate(it)) }
            }

            wordIndex == 0 -> {
                commands.filter { it.startsWith(currentWord) }.forEach { candidates.add(Candidate(it)) }
            }
        }
    }
}
