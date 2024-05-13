/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.app.commanding.commands

data object Help : Command() {
    override val requiresApiKey: Boolean = false
    override suspend fun execute() {
        println("Help: List of available commands...")
        println("/exit - Exits the application")
        println("/clear - Clears the screen")
        println("/assistant-list - Displays all available assistants")
        println("/assistant-install <assistant-id> - Installs an assistant")
        println("/assistant-uninstall <assistant-id> - Uninstalls an assistant")
        println("/assistant-which - Displays the current assistant")
        println("/assistant-info <assistant-id> - Displays the assistant")
        println("/model <model-id> - Set model override affecting all assistants (gpt-3.5-turbo-16k, gpt-4-turbo, etc.)")
        println("/model-clear - Clears the current model override")
        println("/model-which - Displays the current model override")
        println("/thread-new - Creates a new assistant thread")
        println("/thread-which - Displays the current assistant thread")
        println("/thread-list - Lists all assistant threads")
        println("/thread-info <thread-id> - Displays the assistant thread")
        println("/message-list - Lists all messages in the current assistant thread")
        println("/run-list - Lists all runs in the current assistant thread")
        println("/run-step-list - Lists all run steps in the current assistant thread")
        println("/http-log - Displays the last 10 HTTP requests")
        println("/set-key <api-key> - Set your openai api key")
        println("/log-level <level> - Set the log level (ERROR, DEBUG, INFO, OFF)")
        println("/exec <command> - Executes a shell command for convenience")
    }
}

