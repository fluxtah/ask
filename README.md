## Assistant Kommander
**Assistant Kommander** is a Kotlin-based console application that utilizes the OpenAI Assistants API for terminal-based interactions. Developers can execute commands and install assistant plugins via a command line interface.

```bash
 ░▒▓██████▓▒░ ░▒▓███████▓▒░▒▓█▓▒░░▒▓█▓▒░ 
░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░      ░▒▓█▓▒░░▒▓█▓▒░ 
░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░      ░▒▓█▓▒░░▒▓█▓▒░ 
░▒▓████████▓▒░░▒▓██████▓▒░░▒▓███████▓▒░  
░▒▓█▓▒░░▒▓█▓▒░      ░▒▓█▓▒░▒▓█▓▒░░▒▓█▓▒░ 
░▒▓█▓▒░░▒▓█▓▒░      ░▒▓█▓▒░▒▓█▓▒░░▒▓█▓▒░ 
░▒▓█▓▒░░▒▓█▓▒░▒▓███████▓▒░░▒▓█▓▒░░▒▓█▓▒░             
░▒▓█▓▒  Assistant Kommander v0.1  ▒▓█▓▒░

Assistants available:
@coder - Coder Assistant 1.0, installed: true

Type /help for a list of commands

$ @coder how do I make a basic kotlin hello world function?

[Exec Fun] createDirectory: {"directoryName":"KotlinHelloWorld"}...
Creating directory: /ask/KotlinHelloWorld
[Fun Result] {"created":"true"}
[Exec Fun] createFile: {"fileName":"KotlinHelloWorld/Main.kt"}...
Creating file: /ask/KotlinHelloWorld/Main.kt
[Fun Result] {"created":"true"}
[Exec Fun] writeFile: {"fileName":"KotlinHelloWorld/Main.kt","fileContents":"fun main() {\n    println(\"Hello, World!\")\n}"}...
Writing to file: /ask/KotlinHelloWorld/Main.kt
[Fun Result] {"written":"true"}
The Kotlin "Hello World" function has been created in the file `Main.kt` within the `KotlinHelloWorld` project.
$ 
```

### Features
- **Command Line Interface**: Input commands directly through a terminal prompt.
- **Assistant Plugin System**: Allows for the installation and management of AI assistants as plugins.
- **OpenAI API Key Requirement**: Users must configure an OpenAI API key to utilize assistant functionalities.

### Basic Commands
- `/exit` - Exits the application.
- `/assistant-install <assistant-id>` - Installs an assistant plugin.
- `/assistant-list` - Lists all available assistants.
- `/assistant-info <assistant-id>` - Displays details for a specified assistant.
- `/thread-new` - Creates a new assistant thread.
- `/thread-which` - Displays the currently active assistant thread.
- `/thread-list` - Lists all assistant threads.
- `/message-list` - Lists all messages in the current thread.
- `/run-list` - Lists all runs within the current thread.
- `/run-step-list` - Lists steps of all runs within the current thread.
- `/http-log` - Displays the last 10 HTTP requests.
- `/set-key <api-key>` - Sets the OpenAI API key.

### Installing Assistant Plugins
Assistant plugins can be installed using the `/assistant-install <assistant-id>` command. For example, to install the coder assistant, use `/assistant-install coder`.

### Writing Assistant Plugins
Assistant plugins make it easy to define functions that can be executed by an assistant.

Each assistant should implement the `AssistantDefinition` abstract class and assign a class to `functions` property. The `functions` property.

The class you assign to the functions property can define member functions in kotlin, any function you wish to expose should be annotated them with the `@AskFunction` annotation. The annotation should include a description of the function.

Function parameters can be annotated with the `@AskParam` annotation to provide a description of the parameter.

This function will be used to generate the JSON template when creating the assistant with the openai API.

example:- 

```kotlin
class HelloWorldAssistant : AssistantDefinition() {
    override val functions: List<KFunction<*>> = listOf(::createHelloWorldFunction)

    @AskFunction("Create a basic Kotlin Hello World function")
    fun createHelloWorldFunction(): String {
        println("Hello World")
        return "function executed"
    }
}
```

Check the OpenAI assistant documentation for more information on functions.

### Assistant Threads
Assistant threads are used to manage interactions with assistants. Users can create new threads, list existing threads, and switch between them using the `/thread-new`, `/thread-list`, and `/thread-which` commands, respectively.

Generally if you want to start a new conversation with an assistant, you should create a new thread (using `/thread-new`). This allows you to keep track of multiple conversations at once.

### `@` Assistant Commands
With assistant plugins installed, users can interact with them using the `@` symbol.

- `@<assistant-id> <command>` - Directly address an assistant to execute specific commands. For example:
    - `@coder generate me an android project`
    - `@designer create a logo`

### Usage Example
- **Set API Key**: Start by setting your OpenAI API key with `/set-key <api-key>`.
- **Command Execution**: With the application running, `@coder generate me a ktor project` to interact with the coder assistant for generating a Ktor project.
- **Ask Commands**: You can also invoke assistants using the `ask` command. For example, `ask @designer to create a logo`.
