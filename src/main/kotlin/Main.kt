import com.fluxtah.ask.app.App

fun main(args: Array<String>) {
    var interactive = false
    if (args.size > 1) {
        when {
            args[1] == "--version" -> {
                println("0.11")
                return
            }
            args[1] == "--help" -> {
                println("Usage: ask [options]")
                println("Ask is a command line tool for interacting with OpenAI's Assistants API")
                println()
                println("Options:")
                println("  --version        Print the version of the application")
                println("  --help           Print this help message")
                println("  --interactive    Run ask in interactive mode")
                println("  <command>  Run a command in ask, run in interactive mode for available commands to setup first")
                return
            }
            args[1] == "--interactive" -> {
                interactive = true
            }
        }
    }

    val app = App()

    if(interactive) {
        app.run()
    } else {
        if(args.size < 2) {
            println("Usage: ask <command>")
            return
        }
        app.runOneShotCommand(args.drop(1).joinToString(" "))
    }
}
