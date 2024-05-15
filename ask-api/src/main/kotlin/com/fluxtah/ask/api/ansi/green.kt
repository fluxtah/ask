package com.fluxtah.ask.api.ansi

fun green(text: String = "") : String {
    return "\u001b[32m$text"
}

fun blue(text: String = "") : String {
    return "\u001b[34m$text"
}

fun white(text: String = "") : String {
    return "\u001b[37m$text"
}

fun printWhite() {
    print("\u001b[37m")
}
