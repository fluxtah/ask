/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.api

import com.fluxtah.askpluginsdk.Fun
import com.fluxtah.askpluginsdk.FunParam
import com.fluxtah.ask.api.clients.openai.assistants.model.AssistantTool
import kotlin.reflect.KFunction
import kotlin.reflect.KVisibility
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberFunctions

class FunctionToolGenerator {
    fun <T : Any> generateToolsForInstance(targetInstance: T): List<AssistantTool> {
        return targetInstance::class.memberFunctions.filter {
            it.visibility == KVisibility.PUBLIC && it.findAnnotation<Fun>() != null
        }.map { function ->
            val description = function.findAnnotation<Fun>()?.description ?: "No description available"
            AssistantTool.FunctionTool(
                function = AssistantTool.FunctionTool.FunctionSpec(
                    name = function.name,
                    description = description,
                    parameters = createParametersSpec(function)
                )
            )
        }
    }

    private fun createParametersSpec(function: KFunction<*>): AssistantTool.FunctionTool.ParametersSpec {
        val properties = function.parameters.drop(1).associate { parameter ->
            val paramDescription = parameter.findAnnotation<FunParam>()?.description ?: "No specific description"
            (parameter.name ?: throw IllegalArgumentException("Unnamed parameter in function: ${function.name}")) to
                    AssistantTool.FunctionTool.PropertySpec(
                        type = parameter.type.let { type ->
                            when (type.classifier) {
                                String::class -> "string"
                                Int::class -> "integer"
                                Long::class -> "long"
                                Boolean::class -> "boolean"
                                // More types can be added here
                                else -> throw IllegalArgumentException("Unsupported parameter type: $type for function: ${function.name}")
                            }
                        },
                        description = paramDescription
                    )
        }
        return AssistantTool.FunctionTool.ParametersSpec(
            type = "object",
            properties = properties,
            required = properties.keys.toList()
        )
    }
}