/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.api.tools.fn

import com.fluxtah.askpluginsdk.Fun
import com.fluxtah.askpluginsdk.FunParam
import com.fluxtah.ask.api.clients.openai.assistants.model.AssistantTool
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KVisibility
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.memberProperties

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

    private fun createPropertiesSpecForDataClass(kClass: KClass<*>): Map<String, AssistantTool.FunctionTool.PropertySpec> {
        return kClass.memberProperties.associate { property ->
            val description = property.findAnnotation<FunParam>()?.description ?: ""
            property.name to AssistantTool.FunctionTool.PropertySpec(
                type = when (property.returnType.classifier) {
                    String::class -> "string"
                    Int::class -> "integer"
                    Long::class -> "long"
                    Boolean::class -> "boolean"
                    else -> if (property.returnType.classifier is KClass<*>) {
                        "object"
                    } else {
                        throw IllegalArgumentException("Unsupported property type: ${property.returnType} for property: ${property.name}")
                    }
                },
                description = description,
                properties = if (property.returnType.classifier is KClass<*>) {
                    createPropertiesSpecForDataClass(property.returnType.classifier as KClass<*>)
                } else {
                    null
                }
            )
        }
    }

    private fun createParametersSpec(function: KFunction<*>): AssistantTool.FunctionTool.ParametersSpec {
        val properties = function.parameters.drop(1).associate { parameter ->
            val paramDescription = parameter.findAnnotation<FunParam>()?.description ?: "No specific description"
            val parameterType = parameter.type.classifier

            (parameter.name ?: throw IllegalArgumentException("Unnamed parameter in function: ${function.name}")) to
                    if (parameterType is KClass<*> && parameterType.isData) {
                        AssistantTool.FunctionTool.PropertySpec(
                            type = "object",
                            description = paramDescription,
                            properties = createPropertiesSpecForDataClass(parameterType)
                        )
                    } else {
                        AssistantTool.FunctionTool.PropertySpec(
                            type = when (parameterType) {
                                String::class -> "string"
                                Int::class -> "integer"
                                Long::class -> "long"
                                Boolean::class -> "boolean"
                                else -> throw IllegalArgumentException("Unsupported parameter type: $parameterType for function: ${function.name}")
                            },
                            description = paramDescription
                        )
                    }
        }
        return AssistantTool.FunctionTool.ParametersSpec(
            type = "object",
            properties = properties,
            required = properties.keys.toList()
        )
    }
}