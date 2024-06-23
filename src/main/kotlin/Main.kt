/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

import com.fluxtah.ask.Version
import com.fluxtah.ask.api.di.askApiModule
import com.fluxtah.ask.app.ConsoleApplication
import com.fluxtah.ask.app.di.appModule
import com.fluxtah.ask.api.di.commandFactoryModule
import com.fluxtah.ask.api.di.commandsModule
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import java.io.File

suspend fun main(args: Array<String>) {
    val testing = args.contains("--test-plugin") || args.contains("-t")
    val interactive = args.contains("--interactive") || args.contains("-i")
    val help = args.contains("--help") || args.contains("-h")
    val version = args.contains("--version") || args.contains("-v")
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
            println("  --version, -v        Print the version of the application")
            println("  --help, -h           Print this help message")
            println("  --interactive, -i    Run ask in interactive mode")
            println("  --test-plugin, -t    Test a plugin")
            println("  <command>            Run a command in ask, run in interactive mode for available commands to setup first")
            return
        }
    }

    startKoin {
        modules(commandsModule, commandFactoryModule, askApiModule, appModule)
    }

    val consoleApplication: ConsoleApplication = GlobalContext.get().get()

    if (testing) {
        val testPluginArgIndexLong = args.indexOf("--test-plugin")
        val testPluginArgIndexShort = args.indexOf("-t")
        val testPluginArgIndex = if (testPluginArgIndexLong != -1) testPluginArgIndexLong else testPluginArgIndexShort
        val testPluginFilePath = args.getOrNull(testPluginArgIndex + 1)
        if (testPluginFilePath == null) {
            println("Usage: ask --test-plugin <plugin-name>")
            return
        } else {
            val pluginFile = File(testPluginFilePath)
            if (!pluginFile.exists()) {
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
