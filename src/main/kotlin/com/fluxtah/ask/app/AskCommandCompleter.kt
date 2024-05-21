package com.fluxtah.ask.app

import com.fluxtah.ask.api.assistants.AssistantRegistry
import com.fluxtah.ask.api.repository.ThreadRepository
import com.fluxtah.ask.app.commanding.CommandFactory
import com.fluxtah.askpluginsdk.io.getCurrentWorkingDirectory
import org.jetbrains.kotlin.util.prefixIfNot
import org.jline.reader.Candidate
import org.jline.reader.Completer
import org.jline.reader.LineReader
import org.jline.reader.ParsedLine
import java.io.File

class AskCommandCompleter(
    private val assistantRegistry: AssistantRegistry,
    private val commandFactory: CommandFactory,
    private val threadRepository: ThreadRepository
) : Completer {

    private val models = listOf("gpt-3.5-turbo-16k", "gpt-4-turbo", "gpt-4o")

    private val logLevels = listOf("ERROR", "DEBUG", "INFO", "OFF")

    override fun complete(reader: LineReader?, line: ParsedLine?, candidates: MutableList<Candidate>?) {
        if (line == null || candidates == null) return

        val words = line.words()
        val wordIndex = line.wordIndex()
        val currentWord = if (wordIndex < words.size) words[wordIndex] else ""

        when {
            currentWord.startsWith("@") -> {
                assistantRegistry.getAssistants().map { it.id.prefixIfNot("@") }
                    .filter { it.startsWith(currentWord) }
                    .forEach { candidates.add(Candidate(it)) }
            }

            line.line().endsWith("file:") -> {
                if (wordIndex == words.size - 1) {
                    File(getCurrentWorkingDirectory()).listFiles()?.forEach {
                        candidates.add(
                            Candidate(
                                if (it.isDirectory)
                                    "file:${it.name}/"
                                else
                                    "file:${it.name}",
                                if (it.isDirectory)
                                    "${it.name}/"
                                else
                                    it.name,
                                null,
                                null,
                                null,
                                null,
                                !it.isDirectory
                            ),
                        )
                    }
                }
            }

            line.line().trim().matches("file:.+/$".toRegex()) -> {
                if (wordIndex == words.size - 1) {
                    // Properly form the path to include a separator between the directory parts
                    val path = line.line().substringAfterLast("file:").dropLast(1)
                    val fullPath = getCurrentWorkingDirectory() + "/" + path
                    File(fullPath).listFiles()?.forEach {
                        candidates.add(
                            Candidate(
                                if (it.isDirectory)
                                    "file:$path/${it.name}/"
                                else
                                    "file:$path/${it.name}",
                                if (it.isDirectory)
                                    "${it.name}/"
                                else
                                    it.name,
                                null, null, null, null, !it.isDirectory
                            )
                        )
                    }
                }
            }

            words.size > 1 && words[0].startsWith("/assistant-info") ||
                    words[0].startsWith("/assistant-install") ||
                    words[0].startsWith("/assistant-uninstall") -> {
                assistantRegistry.getAssistants().forEach { candidates.add(Candidate(it.id)) }
            }

            words.size > 1 && words[0].startsWith("/model") -> {
                if (wordIndex == 1) {
                    models.forEach { candidates.add(Candidate(it)) }
                }
            }

            words.size > 1 && words[0].startsWith("/log-level") -> {
                if (wordIndex == 1) {
                    logLevels.forEach { candidates.add(Candidate(it)) }
                }
            }

            words.size > 1 && (words[0].startsWith("/thread-delete") ||
                    words[0].startsWith("/thread-switch")) -> {
                if (wordIndex == 1) {
                    threadRepository.listThreads().forEach { candidates.add(Candidate(it.threadId)) }
                }
            }

            wordIndex == 0 -> {
                commandFactory.getCommands().map { "/${it.name}" }.filter { it.startsWith(currentWord) }
                    .forEach { candidates.add(Candidate(it)) }
            }
        }
    }
}
