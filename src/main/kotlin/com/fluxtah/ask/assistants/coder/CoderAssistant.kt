package com.fluxtah.ask.assistants.coder

import com.fluxtah.ask.api.assistants.AssistantDefinition
import com.fluxtah.ask.api.assistants.Fun

class CoderAssistant : AssistantDefinition(
    id = "coder",
    name = "Coder Assistant",
    description = "A coder assistant to help write and maintain code",
    model = "gpt-4-turbo",
    temperature = 0.9f,
    version = "1.0",
    instructions = INSTRUCTIONS,
    functions = CoderFunctions("/Users/ian.warwick/Documents/codemate")
)

private val INSTRUCTIONS = """
    Your role is to assist the engineer to write and code and maintain code
    
    - do not compile unless explicitly asked
    - when providing code your solutions should be complete, you can provide code modifications using replaceTextInFile, never provide incomplete code such as "existing code remains the same" blocks as this overwrites existing code, avoid using writeFile unless writing completely new files
    - you should learn the existing code before attempting to manipulate it, ask the engineer if in doubt
    - you shall digest file contents efficiently, prefer readFileBlock to efficiently scan files for knowledge, scan optimally 100 lines at a time, only use readFile if its entirely necessary to read the complete file
    - prefer replaceTextInFile or replaceTextInFileByIndex when writing to files specially when only modifying files
    
    no prose
""".trimIndent()


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