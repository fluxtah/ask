/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */
package com.fluxtah.ask.api

import com.fluxtah.ask.api.kotlin.KotlinFileRepository
import com.fluxtah.askpluginsdk.io.getCurrentWorkingDirectory
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.psiUtil.startOffset
import kotlin.test.Test

class KotlinFileRepositoryTest {
    @Test
    fun qux() {
        val repository = KotlinFileRepository()
        val ktFile = repository.parseFile(getCurrentWorkingDirectory() + "/src/main/kotlin/com/fluxtah/ask/api/clients/openai/assistants/AssistantsApi.kt")

        val classes = ktFile!!.declarations.filterIsInstance<KtClass>()
        val properties = ktFile.declarations.filterIsInstance<KtProperty>()
        for (property in properties) {
            println("PROP ${property.name} ${property.textRange.startOffset}:${property.textRange.endOffset}")
        }

        val functions = ktFile.declarations.filterIsInstance<KtNamedFunction>()
        for (function in functions) {
            println("  FUN ${function.name} ${function.textRange.startOffset}:${function.textRange.endOffset}")
        }

        for (klass in classes) {
            println("CLASS ${klass.name} ${klass.textRange.startOffset}:${klass.textRange.endOffset}")
            val properties = klass.declarations.filterIsInstance<KtProperty>()
            for (property in properties) {
                println("  PROP ${property.name}: ${property.textRange.startOffset}:${property.textRange.endOffset}")
            }
            val functions = klass.declarations.filterIsInstance<KtNamedFunction>()
            for (function in functions) {
                println("  FUN ${function.name} ${function.textRange.startOffset}:${function.textRange.endOffset}")
                //println("  FUN ${function.name}${function.valueParameterList?.text ?: "()"}: ${function.typeReference?.text ?: "Unit"}")
            }
        }
    }
}