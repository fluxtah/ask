package com.fluxtah.ask.api.parser

class MarkdownParser(private val input: String) {
    private val tokens = mutableListOf<Token>()
    private var pos = 0 // Manually controlled position variable

    fun parse(): List<Token> {
        var insideCodeBlock = false
        val content = StringBuilder()
        var language: String? = null

        while (pos < input.length) {
            val c = input[pos]
            if (input.startsWith("```", pos)) {
                if (!insideCodeBlock) {
                    // Handle start of a code block
                    if (content.isNotEmpty()) {
                        tokens.add(
                            Token.Text(
                                content.toString().dropLastWhile { it == '\n' })
                        ) // Drop trailing newlines from text
                        content.clear()
                    }
                    insideCodeBlock = true
                    val nextNewLine = input.indexOf('\n', pos + 3)
                    language = if (nextNewLine > -1) {
                        input.substring(pos + 3, nextNewLine).trim()
                    } else {
                        null
                    }
                    pos = nextNewLine + 1 // Move pos to the start of the code content
                    continue
                } else {
                    // Handle end of a code block
                    tokens.add(Token.CodeBlock(language, content.toString()))
                    content.clear()
                    insideCodeBlock = false
                    language = null
                    pos += 3 // Skip the closing ```
                    continue
                }
            }

            // Regular text accumulation
            content.append(c)

            pos++ // Increment position after handling the current character
        }

        // Handle any remaining content
        if (content.isNotEmpty()) {
            tokens.add(
                if (insideCodeBlock) Token.CodeBlock(
                    language,
                    content.toString()
                ) else Token.Text(content.toString())
            )
        }

        return tokens
    }
}


