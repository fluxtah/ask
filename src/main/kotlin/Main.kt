/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

import com.fluxtah.ask.Version
import com.fluxtah.ask.app.ConsoleApplication
import java.io.File

fun main(args: Array<String>) {
    val testing = args.contains("--test-plugin")
    val interactive = args.contains("--interactive")
    val help = args.contains("--help")
    val version = args.contains("--version")
    val startsWithProgramName = args.firstOrNull()?.startsWith("ask") ?: false

    when {
        version -> {
            println("Ask Version: ${Version.APP_VERSION}")
            return
        }

        help -> {
            println("Usage: ask [options]")
            println("Ask is a command line tool for interacting with OpenAI's Assistants API")
            println()
            println("Options:")
            println("  --version        Print the version of the application")
            println("  --help           Print this help message")
            println("  --interactive    Run ask in interactive mode")
            println("  <command>        Run a command in ask, run in interactive mode for available commands to setup first")
            return
        }
    }

    val consoleApplication = ConsoleApplication()

    if (testing) {
        val testPluginArgIndex = args.indexOf("--test-plugin")
        val testPluginFilePath = args.getOrNull(testPluginArgIndex + 1)
        if(testPluginFilePath == null) {
            println("Usage: ask --test-plugin <plugin-name>")
            return
        } else {
            val pluginFile = File(testPluginFilePath)
            if(!pluginFile.exists()) {
                println("Plugin not found: $testPluginFilePath")
                return
            }
            consoleApplication.debugPlugin(pluginFile)
        }
    }

    if (interactive) {
        consoleApplication.run()
        return
    }

    if (!startsWithProgramName) {
        consoleApplication.runOneShotCommand(args.joinToString(" "))
        return
    }

    if (args.size < 2) {
        println("Usage: ask <command>")
    } else {
        consoleApplication.runOneShotCommand(args.drop(1).joinToString(" "))
    }
}
