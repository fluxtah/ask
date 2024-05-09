## Assistant Kommander (ALPHA)
**Assistant Kommander** (ASK) is a Kotlin-based application that brings OpenAI Assistants to the console. Developers can talk to AI assistants directly from the terminal, allowing for quick access to information and assistance.

Assistants can be created and installed as plugins allowing developers to share and create their own assistants. 

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

### Running the Application
To run ASK, execute the following command:

```bash
$ ask --interactive
```

#### Basic Commands
- `/exit` - Exits the application.
- `/assistant-install <assistant-id>` - Installs an assistant plugin.
- `/assistant-uninstall <assistant-id>` - Uninstalls an assistant plugin.
- `/assistant-list` - Lists all available assistants.
- `/assistant-which` - Displays the current assistant (the last assistant used).
- `/assistant-info <assistant-id>` - Displays details for a specified assistant.
- `/model <model>` - Set model override affecting all assistants (gpt-3.5-turbo-16k, gpt-4-turbo, etc.)
- `/model-which` - Displays the currently active model override.
- `/model-clear` - Clears the model override.
- `/thread-new` - Creates a new assistant thread.
- `/thread-which` - Displays the currently active assistant thread.
- `/thread-list` - Lists all assistant threads.
- `/message-list` - Lists all messages in the current thread.
- `/run-list` - Lists all runs within the current thread.
- `/run-step-list` - Lists steps of all runs within the current thread.
- `/http-log` - Displays the last 10 HTTP requests.
- `/set-key <api-key>` - Sets the OpenAI API key.
- `/log-level <level>` - Sets the log level (ERROR, DEBUG, INFO, OFF).

### Installing Assistant Plugins
Assistant plugins should be deployed to the `{USER_HOME}/.ask/plugins` directory, once deployed, the assistant plugin can be installed using the `/assistant-install <assistant-id>` command.

ASK ships with an inbuilt assistant plugin called `coder`, which can be installed using the following command:

```bash
$ /assistant-install coder
```

Once installed you can interact with the assistant using the `@coder` command.

```bash
$ @coder generate me a ktor project
```

### Writing Assistant Plugins
Assistant plugins are written in Kotlin and should be compiled to a jar file. The jar file should be placed in the `{USER_HOME}/.ask/plugins` directory.

You can find a simple hello world plugin [here](https://github.com/fluxtah/ask-plugin-hello).

A gradle plugin is provided to simplify the creation of assistant plugins.

In your gradle build file, apply the `ask-gradle-plugin` plugin and add the `com.github.fluxtah.ask-gradle-plugin` dependency.

```kotlin
plugins {
    kotlin("jvm") version "1.9.23"
    id("com.github.johnrengelman.shadow") version "7.1.0"
    id("com.github.fluxtah.ask-gradle-plugin") version "0.4.0"
}
```

The plugin depends on the `shadow` plugin to create a fat jar which is the jar you can deploy to the plugins directory.

In your dependencies block, add the `ask-plugin-sdk` dependency.

```kotlin
dependencies {
    implementation("com.github.fluxtah:ask-plugin-sdk:0.5.0")
}
```

In your projects `META-INF/services` directory, create a file called `com.fluxtah.askpluginsdk.AskPlugin` and add the fully qualified class name of your plugin class.

```
com.example.hello.HelloAssistant
```

Here is an example of a simple assistant plugin.

```kotlin
class HelloAssistantPlugin : AskPlugin {
    override fun createAssistantDefinitions(config: CreateAssistantDefinitionsConfig): List<AssistantDefinition> {
        return listOf(HelloAssistant(config.logger))
    }
}
```

Each assistant should implement the `AssistantDefinition` abstract class and assign a class to `functions` property. The `functions` property.

example:- 

```kotlin
class HelloAssistant(logger: AskLogger) : AssistantDefinition(
    logger = logger,
    id = "hello",
    name = "Hello Assistant",
    description = "A simple assistant that says hello",
    version = "1.0",
    model = "gpt-3.5-turbo",
    temperature = 0.5f,
    instructions = "Say hello to the user",
    functions = HelloFunctions()
)

class HelloFunctions {
    @Fun("Say hello to the user")
    fun hello(input: String): String {
        return "Hello! $input"
    }
}
```

The class you assign to the functions property can define member functions in kotlin, any function you wish to expose should be annotated them with the `@Fun` annotation. The annotation should include a description of the function.

Function parameters can be annotated with the `@FunParam` annotation to provide a description of the parameter.

Functions annotated with `@Fun` will be used to generate the JSON template when creating the assistant with the openai API.

The gradle plugin adds two useful tasks to your project:-
* `testAskPlugin` task will attempt to run ASK with the java remote debugger enabled, you can attach to the debugger from your IDE to debug your plugin, you'll have to install the plugin first and make sure to uninstall it when you're done (for now).
* `deployAskPlugin` task will build a fat jar and copy it to the plugins directory.

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


