package com.fluxtah.ask.api.ansi

fun green(text: String = "") : String {
    return "\u001b[32m$text\u001B[0m"
}

fun blue(text: String = "") : String {
    return "\u001b[34m$text\u001B[0m"
}

fun cyan(text: String = "") : String {
    return "\u001b[36m$text\u001B[0m"
}
