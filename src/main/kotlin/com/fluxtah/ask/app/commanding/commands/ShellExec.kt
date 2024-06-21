/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.app.commanding.commands

import com.fluxtah.ask.api.printers.AskResponsePrinter
import java.io.BufferedReader
import java.io.InputStreamReader

class ShellExec(private val responsePrinter: AskResponsePrinter) : Command() {
    override val requiresApiKey: Boolean = false
    override suspend fun execute(args: List<String>) {
        if (args.isEmpty()) {
            responsePrinter.println("Invalid number of arguments for /exec, expected a shell command following the command")
            return
        }

        val command = args.joinToString(" ")
        executeShellCommand(command)
    }

    private fun executeShellCommand(command: String) {
        try {
            val process = ProcessBuilder(*command.split(" ").toTypedArray()).start()
            BufferedReader(InputStreamReader(process.inputStream)).use { reader ->
                val result = reader.readLines().joinToString("\n")
                responsePrinter.println(result)
            }
            BufferedReader(InputStreamReader(process.errorStream)).use { reader ->
                val error = reader.readLines().joinToString("\n")
                if (error.isNotEmpty()) {
                    responsePrinter.println(error)
                }
            }
        } catch (e: Exception) {
            responsePrinter.println("Shell command error: ${e.message}")
        }
    }
}