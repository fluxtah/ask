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
import com.fluxtah.ask.app.commanding.CommandFactory
import kotlinx.coroutines.runBlocking

class ConsoleOutputRenderer(
    private val responsePrinter: AskResponsePrinter,
    private val commandFactory: CommandFactory,
    private val workingSpinner: WorkingSpinner = WorkingSpinner(),
) {
    fun renderWelcomeMessage() {
        responsePrinter.println()
        responsePrinter.println(
            """
             ░▒▓██████▓▒░ ░▒▓███████▓▒░▒▓█▓▒░░▒▓█▓▒░
            ░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░      ░▒▓█▓▒░░▒▓█▓▒░
            ░▒▓████████▓▒░░▒▓██████▓▒░░▒▓██████▓▒░
            ░▒▓█▓▒░░▒▓█▓▒░      ░▒▓█▓▒░▒▓█▓▒░░▒▓█▓▒░
            ░▒▓█▓▒░░▒▓█▓▒░▒▓███████▓▒░░▒▓█▓▒░░▒▓█▓▒░             
        """.trimIndent()
        )
        responsePrinter.println("░▒▓█▓░ ASSISTANT KOMMANDER v${Version.APP_VERSION} ░▓█▓▒░")
        responsePrinter.println()
        responsePrinter.println("Assistants available:")
        runBlocking {
            commandFactory.create("/assistant-list").execute()
        }
        responsePrinter.println()
        responsePrinter.println("Type /help for a list of commands, to quit press Ctrl+C or type /exit")
    }

    fun renderAssistantResponse(response: String) {
        val markdownParser = MarkdownParser(response)
        val renderedMarkdown = AnsiMarkdownRenderer().render(markdownParser.parse())
        responsePrinter.println()
        responsePrinter.println(renderedMarkdown)
    }

    fun renderAssistantToolCall(details: AssistantRunStepDetails.ToolCalls.ToolCallDetails.FunctionToolCallDetails) {
        responsePrinter.print("\u001b[1A\u001b[2K")
        responsePrinter.println(" ${green("==>")} ${blue(details.function.name)} (${details.function.arguments})")
        responsePrinter.println()
        responsePrinter.println()
    }

    fun renderAssistantMessage(message: Message) {
        responsePrinter.print("\u001b[1A\u001b[2K")
        responsePrinter.println(message.content.joinToString(" ") { it.text.value })
        responsePrinter.println()
        responsePrinter.println()
    }

    fun renderAssistantError(error: RunManagerStatus.Error) {
        responsePrinter.println()
        responsePrinter.println(error.message)
    }

    fun renderAssistantRunStatusChanged(runStatus: RunStatus) {
        responsePrinter.print("\u001b[1A\u001b[2K")
        val indicator = when (runStatus) {
            RunStatus.FAILED,
            RunStatus.CANCELLED,
            RunStatus.EXPIRED -> "x"

            RunStatus.COMPLETED -> green("✔")
            else -> blue(workingSpinner.next())
        }

        responsePrinter.println(" $indicator $runStatus")
    }

}