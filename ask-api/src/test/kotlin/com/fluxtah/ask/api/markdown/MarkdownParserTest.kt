/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */
package com.fluxtah.ask.api.markdown

import kotlin.test.Test
import kotlin.test.assertEquals

class MarkdownParserTest {
    @Test
    fun testMarkdownParser() {
        // Given
        val markdownParser = MarkdownParser("""
            This is a program that prints "Hello, World!" to the console.
            
            ```kotlin
            fun main() {
                println("Hello, World!")
            }
            ```
            
            You should save this code in a file named `HelloWorld.kt` and run it using the Kotlin compiler.
        """.trimIndent())

        // When
        val parsedMarkdown = markdownParser.parse()

        // Then
        // parsedMarkdown.get(0) is the correct type
        assertEquals(
            "This is a program that prints \"Hello, World!\" to the console.\n\n"
            , (parsedMarkdown[0] as Token.Text).content)
        assertEquals(
            "\nfun main() {\n    println(\"Hello, World!\")\n}\n"
            , (parsedMarkdown[1] as Token.CodeBlock).content)
        assertEquals(
            "\n\nYou should save this code in a file named "
            , (parsedMarkdown[2] as Token.Text).content)
        assertEquals("HelloWorld.kt", (parsedMarkdown[3] as Token.Code).content)
        assertEquals(" and run it using the Kotlin compiler.", (parsedMarkdown[4] as Token.Text).content)
    }
}