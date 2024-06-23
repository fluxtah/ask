package com.fluxtah.ask.app

import com.fluxtah.ask.Version
import com.fluxtah.ask.api.RunManagerStatus
import com.fluxtah.ask.api.ansi.blue
import com.fluxtah.ask.api.ansi.green
import com.fluxtah.ask.api.clients.openai.assistants.model.AssistantRunStepDetails
import com.fluxtah.ask.api.clients.openai.assistants.model.Message
import com.fluxtah.ask.api.clients.openai.assistants.model.RunStatus
import com.fluxtah.ask.api.markdown.AnsiMarkdownRenderer
import com.fluxtah.ask.api.markdown.MarkdownParser
import com.fluxtah.ask.api.printers.AskResponsePrinter
import com.fluxtah.ask.api.commanding.CommandFactory
import kotlinx.coroutines.runBlocking

class ConsoleOutputRenderer(
    private val responsePrinter: AskResponsePrinter,
    private val workingSpinner: WorkingSpinner,
) {
    fun renderWelcomeMessage() {
        responsePrinter
            .begin()
            .println()
            .println(
                """
                 ░▒▓██████▓▒░ ░▒▓███████▓▒░▒▓█▓▒░░▒▓█▓▒░
                ░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░      ░▒▓█▓▒░░▒▓█▓▒░
                ░▒▓████████▓▒░░▒▓██████▓▒░░▒▓██████▓▒░
                ░▒▓█▓▒░░▒▓█▓▒░      ░▒▓█▓▒░▒▓█▓▒░░▒▓█▓▒░
                ░▒▓█▓▒░░▒▓█▓▒░▒▓███████▓▒░░▒▓█▓▒░░▒▓█▓▒░             
            """.trimIndent()
            )
            .println("░▒▓█▓░ ASSISTANT KOMMANDER v${Version.APP_VERSION} ░▓█▓▒░")
            .println()
            .println("Type /help for a list of commands, to quit press Ctrl+C or type /exit")
            .end()
    }

    fun renderAssistantResponse(response: String) {
        val markdownParser = MarkdownParser(response)
        val renderedMarkdown = AnsiMarkdownRenderer().render(markdownParser.parse())
        responsePrinter
            .begin()
            .println(renderedMarkdown)
            .end()
    }

    fun renderAssistantToolCall(details: AssistantRunStepDetails.ToolCalls.ToolCallDetails.FunctionToolCallDetails) {
        responsePrinter
            .begin()
            .print("\u001b[1A\u001b[2K")
            .println(" ${green("==>")} ${blue(details.function.name)} (${details.function.arguments})")
            .println()
            .println()
            .end()
    }

    fun renderAssistantMessage(message: Message) {
        responsePrinter
            .begin()
            .print("\u001b[1A\u001b[2K")
            .println(message.content.joinToString(" ") { it.text.value })
            .println()
            .println()
            .end()
    }

    fun renderAssistantError(error: RunManagerStatus.Error) {
        responsePrinter
            .begin()
            .println()
            .println(error.message)
            .end()
    }

    fun renderAssistantRunStatusChanged(runStatus: RunStatus) {
        val indicator = when (runStatus) {
            RunStatus.FAILED,
            RunStatus.CANCELLED,
            RunStatus.EXPIRED -> "x"

            RunStatus.COMPLETED -> green("✔")
            else -> blue(workingSpinner.next())
        }

        responsePrinter
            .begin()
            .print("\u001b[1A\u001b[2K")
            .println(" $indicator $runStatus")
            .end()
    }

}