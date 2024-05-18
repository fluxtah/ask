/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */
package com.fluxtah.ask.api.markdown

import com.fluxtah.ask.api.ansi.blue
import com.fluxtah.ask.api.ansi.cyan

class AnsiMarkdownRenderer {
    fun render(tokens: List<Token>): String {
        val builder = StringBuilder()
        tokens.forEach { token ->
            when (token) {
                is Token.CodeBlock -> {
                    builder.appendLine(cyan(token.content.trim()))
                }

                is Token.Text -> {
                    builder.append(token.content)
                }

                is Token.Code -> {
                    builder.append(cyan(token.content))
                }

                is Token.Bold -> {
                    builder.append("\u001B[1m${token.content}\u001B[0m")
                }
            }
        }

        return builder.toString()
    }
}