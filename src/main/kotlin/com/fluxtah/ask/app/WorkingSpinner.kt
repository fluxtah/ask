package com.fluxtah.ask.app

class WorkingSpinner {
    private val loadingChars = listOf("|", "/", "-", "\\")
    private var loadingCharIndex = 0

    fun next(): String {
        loadingCharIndex = (loadingCharIndex + 1) % loadingChars.size
        return loadingChars[loadingCharIndex]
    }
}
