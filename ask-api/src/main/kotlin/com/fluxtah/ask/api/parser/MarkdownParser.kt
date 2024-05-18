package com.fluxtah.ask.api.parser

class MarkdownParser(private val input: String) {
    private val tokens = mutableListOf<Token>()
    private var pos = 0 // Manually controlled position variable
    private val buffer = StringBuilder()
    fun parse(): List<Token> {
        while (pos < input.length) {
            val c = input[pos]
            buffer.append(c)

            if (buffer.endsWith("```")) {
                tokens.add(Token.Text(buffer.dropLast(3).toString()))
                buffer.clear()
                val language = readLanguage()
                readCodeBlock(language)
            } else if(input.startsWith("`", pos) && !input.startsWith("``", pos)) {
                tokens.add(Token.Text(buffer.dropLast(1).toString()))
                buffer.clear()
                pos++
                while (pos < input.length) {
                    val c = input[pos]
                    buffer.append(c)
                    if (buffer.endsWith("`")) {
                        tokens.add(Token.Code(buffer.dropLast(1).toString()))
                        buffer.clear()
                        break
                    }
                    pos++
                }
            }

            pos++ // Increment position after handling the current character
        }

        if(buffer.isNotEmpty()) {
            tokens.add(Token.Text(buffer.toString()))
            buffer.clear()
        }

        return tokens
    }

    fun readLanguage(): String {
        val language = StringBuilder()
        while (pos < input.length) {
            val c = input[pos]
            if (c == '\n') {
                break
            }
            language.append(c)
            pos++
        }
        return language.toString()
    }

    fun readCodeBlock(language: String) {
        val codeBlock = StringBuilder()
        while (pos < input.length) {
            val c = input[pos]
            codeBlock.append(c)
            if (codeBlock.endsWith("```")) {
                tokens.add(Token.CodeBlock(language, codeBlock.dropLast(3).toString()))
                break
            }
            pos++
        }
    }
}


