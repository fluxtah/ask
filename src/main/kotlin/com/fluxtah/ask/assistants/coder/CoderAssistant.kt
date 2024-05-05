/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.assistants.coder

import com.fluxtah.askpluginsdk.AssistantDefinition
import com.fluxtah.ask.api.io.getCurrentWorkingDirectory

class CoderAssistant : AssistantDefinition(
    id = "coder",
    name = "Coder Assistant",
    description = "A coder assistant to help write and maintain code",
    model = "gpt-3.5-turbo-16k",
    temperature = 0.9f,
    version = "0.1",
    instructions = INSTRUCTIONS,
    functions = CoderFunctions(getCurrentWorkingDirectory())
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