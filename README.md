## Assistant Kommander (ALPHA)
**Assistant Kommander** (ASK) is a Kotlin-based application that brings [OpenAI Assistants](https://platform.openai.com/docs/assistants/overview) to the terminal. Users can direct prompts to AI assistants from the terminal targeting which assistant they wish to address with `@`.

```bash
$ @koder how do I make a basic kotlin hello world function?

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
```

Assistants can be created and installed as plugins allowing developers to share and create their own assistants.

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

#### Setting your OpenAI API Key
Before you can interact with the assistants, you need to set your OpenAI API key. You can do this by running the following command:

```bash
$ /set-key <api-key>
```

#### Some Basic Commands
- `/set-key <api-key>` - Sets the OpenAI API key.
- `/assistant-install <assistant-id>` - Installs an assistant plugin.
- `/assistant-uninstall <assistant-id>` - Uninstalls an assistant plugin.
- `/assistant-list` - Lists all available assistants.
- `/model <model>` - Set model override affecting all assistants (gpt-3.5-turbo-16k, gpt-4-turbo, etc.)
- `/thread-new` - Creates a new assistant thread.
- `/log-level <level>` - Sets the log level (ERROR, DEBUG, INFO, OFF).
- 
To see a list of all  commands run ask and use `/help`.

### Installing Assistant Plugins
Assistant plugins should be deployed to the `{USER_HOME}/.ask/plugins` directory, once deployed, the assistant plugin can be installed using the `/assistant-install <assistant-id>` command.

ASK ships with an inbuilt assistant plugin called `koder`, which can be installed using the following command:

```bash
$ /assistant-install koder
```

Once installed you can interact with the assistant using the `@koder` command.

```bash
$ @koder generate me a ktor project
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
com.example.hello.HelloAssistantPlugin
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

The class you assign to the functions property can define member functions in kotlin, any function you wish to expose to your assistant should be annotated with the `@Fun` annotation. The annotation should include a description of the function.

Function parameters can be annotated with the `@FunParam` annotation to provide a description of the parameter.

Functions annotated with `@Fun` will be used to generate the JSON  tool function templates when creating the assistant with the Assistants API.

The gradle plugin adds two useful tasks to your project:-
* `testAskPlugin` task will attempt to run ASK with the java remote debugger enabled, you can attach to the debugger from your IDE to debug your plugin, you'll have to install the plugin first and make sure to uninstall it when you're done (for now).
* `deployAskPlugin` task will build a fat jar and copy it to the plugins directory.

### Assistant Threads
Assistant threads are used to manage interactions with assistants. Users can create new threads, list existing threads, and switch between them using the `/thread-new`, `/thread-list`, and `/thread-which` commands, respectively.

Generally if you want to start a new conversation with an assistant, you should create a new thread (using `/thread-new`).

### Non-Interactive Mode
ASK can be run in non-interactive mode by passing a command as an argument.

```bash
$ ask @koder generate me a ktor project
```

Once the command is executed, ASK will exit.

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

## Related Projects
* [Ask Gradle Plugin](https://github.com/fluxtah/ask-gradle-plugin) - For developing your own ask plugins with some useful tasks for testing and local deployment.
* [Ask Plugin SDK](https://github.com/fluxtah/ask-plugin-sdk) - SDK dependency for base plugin interface and assistant definitions.
* [Homebrew ASK](https://github.com/fluxtah/homebrew-ask) - The homebrew tap source.
* [Plugin Hello World Example](https://github.com/fluxtah/ask-plugin-hello) - Example plugin project.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.


