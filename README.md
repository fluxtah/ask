## Assistant Kommander (ALPHA)
**Assistant Kommander** (ASK) is a Kotlin-based application that brings OpenAI Assistants to the console. Developers can talk to AI assistants directly from the terminal, allowing for quick access to information and assistance.

Assistants are installed as plugins allowing developers to share and create their own assistants. The application is designed to be extensible and easy to use.

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
- **Command Line Interface**: Talk to AI assistants directly from the terminal.
- **Assistant Plugin System**: Allows for the installation and management of AI assistants as plugins.
- **Assistant Threads**: Manage multiple assistant conversations with threads.
- **Talk to Assistants**: Directly address assistants using the `@` symbol.
- **Interactive Mode**: Run the application in interactive mode to interact with assistants. `ask --interactive` until you exit with `/exit`.
- **Ask Commands**: One shot commands to interact with assistants using `ask @coder to generate a ktor project`.


### Installation With Homebrew

To install `ask` using Homebrew, follow these simple steps:

1. **Add the Tap**:
   First, add the custom tap to your Homebrew repository list. This informs Homebrew where to find the `ask` formula.
   ```bash
   brew tap fluxtah/ask
   ```

2. **Install the Tool**:
   After adding the tap, install `ask` like any other Homebrew package:
   ```bash
   brew install ask
   ```

#### Uninstallation With Homebrew

If you need to uninstall `ask`, use the following command:
```bash
brew uninstall ask
```

### Basic Commands
- `/exit` - Exits the application.
- `/assistant-install <assistant-id>` - Installs an assistant plugin.
- `/assistant-uninstall <assistant-id>` - Uninstalls an assistant plugin.
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

The class you assign to the functions property can define member functions in kotlin, any function you wish to expose should be annotated them with the `@Fun` annotation. The annotation should include a description of the function.

Function parameters can be annotated with the `@FunParam` annotation to provide a description of the parameter.

This function will be used to generate the JSON template when creating the assistant with the openai API.

example:- 

```kotlin
class HelloAssistant : AssistantDefinition(
  id = "hello",
  name = "Hello Assistant",
  description = "A simple assistant to say hello",
  model = "gpt-4-turbo",
  temperature = 0.9f,
  version = "1.0",
  instructions = "Just say hello",
  functions = HelloFunctions()
)

class HelloFunctions {
  @Fun("Greets the user with a hello")
  fun hello() = "Hello!"
}
```

When defining which functions are available to the assistant, you can use the `@Fun` annotation to provide a description of the function.

For now until we have an external modular way of loading plugins you can register your assistant in the `App` class `init` block.

```kotlin
assistantRegistry.register(HelloAssistant())
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

### Ask Application Properties
Ask stores property files and other data in a `.ask` directory in the user's home directory. This file persists properties such as openai API key and context data such as current thread and installed assistants.

### Development
Development is done in the intellij environment using the gradle build system.

#### Building a distribution
To build a distribution, run the following command:

```bash
./gradlew packageDistribution
```

Will create a tar file in the `build/dist` directory. ie:- ask-0.1.tar.gz, the tar file can then
be distributed with homebrew, etc.

Homebrew tap is available at [Homebrew Ask](https://github.com/fluxtah/homebrew-ask)

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.


