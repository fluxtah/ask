/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.api.kotlin

import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.com.intellij.openapi.Disposable
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.com.intellij.psi.PsiFileFactory
import org.jetbrains.kotlin.idea.KotlinFileType
import java.nio.file.Files
import java.nio.file.Paths

class KotlinFileRepository {
    private val disposable: Disposable = Disposer.newDisposable()
    private val environment = setupKotlinEnvironment()

    private fun setupKotlinEnvironment(): KotlinCoreEnvironment {
        val configuration = CompilerConfiguration().apply {
            put(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)
        }
        return KotlinCoreEnvironment.createForProduction(disposable, configuration, EnvironmentConfigFiles.JVM_CONFIG_FILES)
    }

    fun parseFile(filePath: String): KtFile? {
        val fileContent = try {
            Files.readString(Paths.get(filePath))
        } catch (e: Exception) {
            println("Error reading file: $e")
            return null
        }

        val psiFile = PsiFileFactory.getInstance(environment.project).createFileFromText("temp.kt", KotlinFileType.INSTANCE, fileContent)
        return psiFile as? KtFile
    }

    fun disposeResources() {
        Disposer.dispose(disposable)
    }
}
