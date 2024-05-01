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

### `@` Assistant Commands
With assistant plugins installed, users can interact with them using the `@` symbol.

- `@<assistant-id> <command>` - Directly address an assistant to execute specific commands. For example:
    - `@coder generate me an android project`
    - `@designer create a logo`

### Usage Example
- **Set API Key**: Start by setting your OpenAI API key with `/set-key <api-key>`.
- **Command Execution**: With the application running, `@coder generate me a ktor project` to interact with the coder assistant for generating a Ktor project.
- **Ask Commands**: You can also invoke assistants using the `ask` command. For example, `ask @designer to create a logo`.
