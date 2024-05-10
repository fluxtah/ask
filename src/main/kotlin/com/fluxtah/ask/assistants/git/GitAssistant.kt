package com.fluxtah.ask.assistants.git

import com.fluxtah.askpluginsdk.AssistantDefinition
import com.fluxtah.askpluginsdk.Fun
import com.fluxtah.askpluginsdk.FunParam
import com.fluxtah.askpluginsdk.logging.AskLogger
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class GitAssistant(logger: AskLogger) : AssistantDefinition(
    logger = logger,
    id = "git",
    name = "Git Assistant",
    description = "A Git assistant to handle version control operations",
    model = "gpt-3.5-turbo-16k",
    temperature = 0.9f,
    version = "0.1",
    instructions = INSTRUCTIONS,
    functions = GitFunctions(logger)
)

private val INSTRUCTIONS = """
    Your role is to assist with version control using Git:
    - Execute Git commands like status, commit, push, and pull.
    - Provide outputs of Git operations.
    - Ensure secure handling of repository credentials.
    - Simplify common Git workflows.
""".trimIndent()

class GitFunctions(val logger: AskLogger) {

    private fun executeGitCommand(directory: String, command: String): String {
        val processBuilder = ProcessBuilder(*command.split(" ").toTypedArray())
        processBuilder.directory(File(directory))
        processBuilder.redirectErrorStream(true)

        return try {
            val process = processBuilder.start()
            val results = process.inputStream.bufferedReader().readText()
            process.waitFor()
            Json.encodeToString(
                mapOf(
                    "success" to "true",
                    "output" to results
                )
            )
        } catch (e: Exception) {
            Json.encodeToString(
                mapOf(
                    "success" to "false",
                    "error" to e.message
                )
            )
        }
    }

    @Fun("Checks the status of a Git repository")
    fun checkStatus(
        @FunParam("The directory path of the Git repository")
        directory: String
    ): String = executeGitCommand(directory, "git status")

    @Fun("Commits changes to a Git repository")
    fun commitChanges(
        @FunParam("The directory path of the Git repository")
        directory: String,
        @FunParam("The commit message")
        message: String
    ): String = executeGitCommand(directory, "git commit -m \"$message\"")

    @Fun("Pulls the latest changes from the remote repository")
    fun pullChanges(
        @FunParam("The directory path of the Git repository")
        directory: String
    ): String = executeGitCommand(directory, "git pull")

    @Fun("Pushes local commits to the remote repository")
    fun pushChanges(
        @FunParam("The directory path of the Git repository")
        directory: String
    ): String = executeGitCommand(directory, "git push")

    @Fun("Adds files to staging")
    fun addFilesToStage(
        @FunParam("The directory path of the Git repository")
        directory: String,
        @FunParam("Files to add")
        files: String  // Use "." to add all changed files
    ): String = executeGitCommand(directory, "git add $files")

    @Fun("Creates a new branch in a Git repository")
    fun createBranch(
        @FunParam("The directory path of the Git repository")
        directory: String,
        @FunParam("The name of the new branch")
        branchName: String
    ): String = executeGitCommand(directory, "git checkout -b $branchName")
}
