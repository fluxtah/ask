package com.fluxtah.ask.api

sealed class RunResult {
    data class Complete(
        val runId: String,
        val responseText: String
    ) : RunResult()

    data class Error(val message: String) : RunResult()
}