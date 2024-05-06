package com.fluxtah.askplugin.hello

import com.fluxtah.askpluginsdk.AskPlugin
import com.fluxtah.askpluginsdk.AssistantDefinition
import com.fluxtah.askpluginsdk.Fun

class HelloAssistantPlugin : AskPlugin {
    override fun createAssistantDefinition() = HelloAssistant()
}

class HelloAssistant : AssistantDefinition(
    id = "hello",
    name = "Hello Assistant",
    description = "A simple assistant that says hello",
    version = "1.0",
    model = "gpt-3.5-turbo",
    temperature = 0.5f,
    instructions = "Say hello to the assistant",
    functions = HelloFunctions()
)

class HelloFunctions {
    @Fun("Say hello")
    fun hello() = "Hello!"
}
