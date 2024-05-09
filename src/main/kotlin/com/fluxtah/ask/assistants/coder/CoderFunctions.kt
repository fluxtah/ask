/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.assistants.coder

import com.fluxtah.askpluginsdk.Fun
import com.fluxtah.askpluginsdk.FunParam
import com.fluxtah.askpluginsdk.logging.AskLogger
import com.fluxtah.askpluginsdk.logging.LogLevel
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.gradle.tooling.GradleConnector
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.file.Paths

class CoderFunctions(val logger: AskLogger, private val baseDir: String) {

    init {
        // Ensure the base directory exists
        File(baseDir).mkdirs()
    }

    private fun getSafeFile(path: String): File {
        val normalizedPath = Paths.get(baseDir, path).normalize().toFile()
        if (!normalizedPath.absolutePath.startsWith(baseDir)) {
            throw SecurityException("Attempt to access outside of the base directory")
        }
        return normalizedPath
    }

    @Fun("Creates a directory for a software project")
    fun createDirectory(
        @FunParam("The desired relative path and name of the project if it does not exist already")
        directoryName: String
    ): String {
        return try {
            val directory = getSafeFile(directoryName)
            if (directory.mkdirs()) {
                logger.log(LogLevel.INFO, "Creating directory: ${directory.path}")
                Json.encodeToString(
                    mapOf(
                        "created" to "true",
                    )
                )
            } else {
                Json.encodeToString(
                    mapOf(
                        "created" to "false",
                        "error" to "Directory already exists or cannot be created"
                    )
                )
            }
        } catch (e: Exception) {
            Json.encodeToString(
                mapOf(
                    "created" to "false",
                    "error" to e.message
                )
            )
        }
    }

    @Fun("Creates a file for a software project")
    fun createFile(
        @FunParam("The desired relative path of the file")
        fileName: String
    ): String {
        return try {
            val file = getSafeFile(fileName)
            if (file.createNewFile()) {
                logger.log(LogLevel.INFO, "Creating file: ${file.path}")
                Json.encodeToString(
                    mapOf(
                        "created" to "true",
                    )
                )
            } else {
                Json.encodeToString(
                    mapOf(
                        "created" to "false",
                        "error" to "File already exists or cannot be created"
                    )
                )
            }
        } catch (e: Exception) {
            Json.encodeToString(
                mapOf(
                    "created" to "false",
                    "error" to e.message
                )
            )
        }
    }


    @Fun("Creates or writes to a file for a software project")
    fun writeFile(
        @FunParam("The relative project path of the file")
        fileName: String,
        @FunParam("The contents to write to the file")
        fileContents: String
    ): String {
        return try {
            val file = getSafeFile(fileName)
            file.writeText(fileContents)
            logger.log(LogLevel.INFO, "Writing to file: ${file.path}")
            Json.encodeToString(
                mapOf(
                    "written" to "true",
                )
            )
        } catch (e: Exception) {
            Json.encodeToString(
                mapOf(
                    "written" to "false",
                    "error" to e.message
                )
            )
        }
    }

    @Fun("Reads a file for a software project")
    fun readFile(
        @FunParam("The relative project path of the file")
        fileName: String
    ): String {
        return try {
            val file = getSafeFile(fileName)
            file.readText()
        } catch (e: Exception) {
            Json.encodeToString(
                mapOf(
                    "read" to "false",
                    "error" to e.message
                )
            )
        }
    }

    @Fun("Reads a block of lines from a file for a software project")
    fun readFileBlock(
        @FunParam("The relative project path of the file")
        fileName: String,
        @FunParam("The line number to start reading from")
        startLine: Int,
        @FunParam("The number of lines to read")
        lineCount: Int
    ): String {
        return try {
            val file = getSafeFile(fileName)
            val lines = file.useLines { it.drop(startLine - 1).take(lineCount).toList() }
            val block = lines.joinToString("\n")
            logger.log(LogLevel.INFO, "[Read File Block] $block")
            block
        } catch (e: Exception) {
            Json.encodeToString(
                mapOf(
                    "read" to "false",
                    "error" to e.message
                )
            )
        }
    }

    @Fun("Counts the number of lines in a file for a software project")
    fun countLinesInFile(
        @FunParam("The relative project path of the file")
        fileName: String
    ): String {
        return try {
            val file = getSafeFile(fileName)
            val lineCount = file.useLines { lines -> lines.count() }
            Json.encodeToString(
                mapOf(
                    "lineCount" to lineCount.toString(),
                )
            )
        } catch (e: Exception) {
            Json.encodeToString(
                mapOf(
                    "error" to e.message
                )
            )
        }
    }

    @Fun("Lists files in a directory for a software project")
    fun listFilesInDirectory(
        @FunParam("The relative project path of the directory")
        directoryName: String
    ): String {
        return try {
            val directory = getSafeFile(directoryName)
            val fileList = listFilesInImmediateSubdirectories(directory)
                .map { it.replace("$baseDir/", "") }
            Json.encodeToString(
                mapOf(
                    "files" to fileList,
                )
            )
        } catch (e: Exception) {
            Json.encodeToString(
                mapOf(
                    "error" to e.message
                )
            )
        }
    }

    private fun listFilesInImmediateSubdirectories(file: File): List<String> {
        val fileList = mutableListOf<String>()
        if (file.isDirectory) {
            file.listFiles()?.forEach { subfile ->
                fileList.add(subfile.absolutePath)
                if (subfile.isDirectory) {
                    subfile.listFiles()?.forEach { innerFile ->
                        if (innerFile.isFile || innerFile.isDirectory) {
                            fileList.add(innerFile.absolutePath)
                        }
                    }
                }
            }
        }
        return fileList
    }

    @Fun("Replaces specific text in a file for a software project")
    fun replaceTextInFile(
        @FunParam("The relative project path of the file")
        fileName: String,
        @FunParam("The text to replace")
        textToReplace: String,
        @FunParam("The replacement text")
        replacementText: String
    ): String {
        return try {
            val file = getSafeFile(fileName)
            val fileText = file.readText()

            if (fileText.contains(textToReplace)) {
                val newText = fileText.replace(textToReplace, replacementText)
                file.writeText(newText)
                logger.log(LogLevel.INFO, "Replaced specific text in file: ${file.path}")
                Json.encodeToString(
                    mapOf(
                        "replaced" to "true",
                    )
                )
            } else {
                Json.encodeToString(
                    mapOf(
                        "replaced" to "false",
                        "error" to "Text to replace not found"
                    )
                )
            }
        } catch (e: Exception) {
            Json.encodeToString(
                mapOf(
                    "replaced" to "false",
                    "error" to e.message
                )
            )
        }
    }

    @Fun("Replaces text in a file by index for a software project")
    fun replaceTextInFileByIndex(
        @FunParam("The relative project path of the file")
        fileName: String,
        @FunParam("The start index of the text to replace")
        startIndex: Int,
        @FunParam("The end index of the text to replace")
        endIndex: Int,
        @FunParam("The replacement text")
        replacementText: String
    ): String {
        return try {
            val file = getSafeFile(fileName)
            val fileText = file.readText()

            if (startIndex in 0 until endIndex && endIndex <= fileText.length) {
                val newText = StringBuilder(fileText)
                    .replace(startIndex, endIndex, replacementText)
                    .toString()
                file.writeText(newText)
                logger.log(LogLevel.INFO, "Replaced content in file: ${file.path}")
                Json.encodeToString(
                    mapOf(
                        "replaced" to "true",
                    )
                )
            } else {
                Json.encodeToString(
                    mapOf(
                        "replaced" to "false",
                        "error" to "Invalid start or end index"
                    )
                )
            }
        } catch (e: Exception) {
            Json.encodeToString(
                mapOf(
                    "replaced" to "false",
                    "error" to e.message
                )
            )
        }
    }

    @Fun("Builds a software project with Gradle")
    fun execGradle(
        @FunParam("The relative project path of the project")
        projectDir: String,
        @FunParam("The Gradle tasks to execute")
        gradleTasks: String = "",
        @FunParam("Additional arguments to pass to Gradle")
        gradleArgs: String
    ): String {
        val errorOut = ByteArrayOutputStream()
        return try {
            val projectDirectory = getSafeFile(projectDir)
            val connector = GradleConnector.newConnector().forProjectDirectory(projectDirectory)
            connector.connect().use { connection ->
                val build = connection.newBuild()
                // Add your tasks like 'clean', 'build', etc.
                build.forTasks(*gradleTasks.split(" ").toTypedArray())

                // Pass additional arguments
                build.withArguments(*gradleArgs.split(" ").toTypedArray())
                build.setStandardOutput(System.out)
                build.setStandardError(errorOut)
                build.run() // This will execute the build with the specified arguments
            }
            Json.encodeToString(
                mapOf(
                    "success" to "true",
                )
            )
        } catch (e: Exception) {
            logger.log(LogLevel.ERROR, "Error executing Gradle: ${e.cause}")
            Json.encodeToString(
                mapOf(
                    "success" to "false",
                    "errorMessage" to e.message,
                    "cause" to e.cause.toString(),
                    "errorOut" to errorOut.toString()
                )
            )
        }
    }
}
