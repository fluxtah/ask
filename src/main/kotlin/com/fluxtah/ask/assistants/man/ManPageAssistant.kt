package com.fluxtah.ask.assistants.man

import com.fluxtah.askpluginsdk.AssistantDefinition
import com.fluxtah.askpluginsdk.Fun
import com.fluxtah.askpluginsdk.FunParam
import com.fluxtah.askpluginsdk.logging.AskLogger
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.InputStreamReader

class ManPageAssistant(logger: AskLogger) : AssistantDefinition(
    logger = logger,
    id = "man",
    name = "Man Page Reader",
    description = "Assists in reading and summarizing man pages",
    model = "gpt-3.5-turbo-16k",
    temperature = 0.9f,
    version = "0.1",
    instructions = INSTRUCTIONS,
    functions = ManPageFunctions(logger)
)

private val INSTRUCTIONS = """
    Your role is to help users quickly understand command usage and options from man pages.
    - Provide summaries of man pages.
    - Answer specific questions about command options and usages.
    - You should handle requests efficiently, extracting only necessary information from man pages.
    - Use concise language to maximize clarity.
""".trimIndent()

class ManPageFunctions(val logger: AskLogger) {

    @Fun("Fetches and summarizes a man page")
    fun summarizeManPage(
        @FunParam("The command to fetch the man page for")
        command: String
    ): String {
        return try {
            val processBuilder = ProcessBuilder("man", command)
            val process = processBuilder.start()
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val lines = reader.readLines()

            // Here, implement logic to summarize or extract key sections
            val summary = lines.joinToString("\n") // Placeholder for summary logic

            Json.encodeToString(
                mapOf(
                    "summary" to summary,
                    "command" to command
                )
            )
        } catch (e: Exception) {
            Json.encodeToString(
                mapOf(
                    "error" to "Could not fetch the man page for the command: $command",
                    "exceptionMessage" to e.message
                )
            )
        }
    }

    @Fun("Answers specific questions about a command from its man page")
    fun getCommandDetail(
        @FunParam("The command to query")
        command: String,
        @FunParam("The detail or option to explain")
        detail: String
    ): String {
        return try {
            val processBuilder = ProcessBuilder("man", command)
            val process = processBuilder.start()
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val lines = reader.readLines()

            // Logic to find and return the specific detail from the man page
            val relevantDetails = lines.filter { it.contains(detail) }.joinToString("\n")

            Json.encodeToString(
                mapOf(
                    "detail" to relevantDetails,
                    "command" to command
                )
            )
        } catch (e: Exception) {
            Json.encodeToString(
                mapOf(
                    "error" to "Could not fetch details from man page for the command: $command",
                    "exceptionMessage" to e.message
                )
            )
        }
    }
}
