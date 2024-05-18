package com.fluxtah.ask.api.markdown

sealed class Token {
    data class Text(val content: String) : Token()
    data class CodeBlock(val language: String?, val content: String) : Token()
    data class Code(val content: String) : Token()
    data class Bold(val content: String) : Token()
}
