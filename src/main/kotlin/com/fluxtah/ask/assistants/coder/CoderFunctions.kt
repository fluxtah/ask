package com.fluxtah.ask.assistants.coder

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.gradle.tooling.GradleConnector
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.file.Paths

class CoderFunctions(private val baseDir: String) {

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

    fun createDirectory(directoryName: String): String {
        return try {
            val directory = getSafeFile(directoryName)
            if (directory.mkdirs()) {
                println("Creating directory: ${directory.path}")
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

    fun createFile(fileName: String): String {
        return try {
            val file = getSafeFile(fileName)
            if (file.createNewFile()) {
                println("Creating file: ${file.path}")
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

    fun writeFile(fileName: String, fileContents: String): String {
        return try {
            val file = getSafeFile(fileName)
            file.writeText(fileContents)
            println("Writing to file: ${file.path}")
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

    fun readFile(fileName: String): String {
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

    fun readFileBlock(fileName: String, startLine: Int, lineCount: Int): String {
        return try {
            val file = getSafeFile(fileName)
            val lines = file.useLines { it.drop(startLine - 1).take(lineCount).toList() }
            val block = lines.joinToString("\n")
            println(block)
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

    fun countLinesInFile(fileName: String): String {
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

    fun listFilesInDirectory(directoryName: String): String {
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

    fun replaceTextInFile(fileName: String, textToReplace: String, replacementText: String): String {
        return try {
            val file = getSafeFile(fileName)
            val fileText = file.readText()

            if (fileText.contains(textToReplace)) {
                val newText = fileText.replace(textToReplace, replacementText)
                file.writeText(newText)
                println("Replaced specific text in file: ${file.path}")
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

    fun replaceTextInFileByIndex(fileName: String, startIndex: Int, endIndex: Int, replacementText: String): String {
        return try {
            val file = getSafeFile(fileName)
            val fileText = file.readText()

            if (startIndex in 0 until endIndex && endIndex <= fileText.length) {
                val newText = StringBuilder(fileText)
                    .replace(startIndex, endIndex, replacementText)
                    .toString()
                file.writeText(newText)
                println("Replaced content in file: ${file.path}")
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

    fun execGradle(projectDir: String, gradleTasks: String = "", gradleArgs: String): String {
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
            println("Error executing Gradle: ${e.cause}")
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
