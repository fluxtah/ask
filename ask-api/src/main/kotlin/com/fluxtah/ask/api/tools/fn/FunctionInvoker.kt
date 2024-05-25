/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.api.tools.fn

import com.fluxtah.ask.api.clients.openai.assistants.model.AssistantRunStepDetails.ToolCalls.ToolCallDetails.FunctionToolCallDetails
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import kotlinx.serialization.serializer
import java.lang.reflect.InvocationTargetException
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.javaConstructor
import kotlin.reflect.jvm.jvmErasure

class FunctionInvoker {
    fun <T : Any> invokeFunction(targetInstance: T, callDetails: FunctionToolCallDetails): String {
        val function = targetInstance::class.memberFunctions.find { it.name == callDetails.function.name }
            ?: throw IllegalArgumentException("Function not found: ${callDetails.function.name}")

        try {
            val argsMap = Json.decodeFromString<Map<String, JsonElement>>(callDetails.function.arguments.replace("\\$", "$"))
            val args = prepareArguments(function, argsMap)
            val result = function.call(targetInstance, *args)

            return result.toString()
//            // Check if the result is @Serializable
//            if (result != null && function.returnType.jvmErasure.findAnnotation<Serializable>() != null) {
//                // If the result type is serializable, encode it to JSON string
//                val serializer = Json.serializersModule.serializer(result::class.java)
//                return Json.encodeToString(serializer, result)
//            } else {
//                // Otherwise, return the result as a string
//                return result.toString()
//            }
        } catch (e: Exception) {
            println("Error decoding arguments: ${callDetails.function.arguments}")
            throw e
        }
    }

    private fun prepareArguments(function: KFunction<*>, argsMap: Map<String, JsonElement>): Array<Any?> {
        return function.parameters.drop(1).map { parameter ->
            val paramName =
                parameter.name ?: throw IllegalArgumentException("Unnamed parameter in function: ${function.name}")
            val jsonElement =
                argsMap[paramName] ?: throw IllegalArgumentException("Argument not found for parameter: $paramName")
            deserializeArgument(jsonElement, parameter.type, parameter)
        }.toTypedArray()
    }

    fun decodeFromString(jsonString: String, type: KType): Any? {
        val serializer = Json.serializersModule.serializer(type)
        return Json.decodeFromString(serializer, jsonString)
    }

    private fun deserializeArgument(jsonElement: JsonElement, type: KType, parameter: KParameter): Any? {
        return when (type.classifier) {
            String::class -> jsonElement.jsonPrimitive.safeContentOrNull() ?: ""
            Int::class -> jsonElement.jsonPrimitive.safeIntOrNull() ?: 0
            Long::class -> jsonElement.jsonPrimitive.safeLongOrNull() ?: 0L
            Boolean::class -> jsonElement.jsonPrimitive.safeBooleanOrNull() ?: false
            else -> {
                decodeFromString(jsonElement.toString(), type)
            }
        }
    }

    private fun JsonPrimitive.safeContentOrNull(): String? = if (isString) content else null
    private fun JsonPrimitive.safeIntOrNull(): Int? = try {
        int
    } catch (e: Exception) {
        null
    }

    private fun JsonPrimitive.safeLongOrNull(): Long? = try {
        long
    } catch (e: Exception) {
        null
    }

    private fun JsonPrimitive.safeBooleanOrNull(): Boolean? = try {
        boolean
    } catch (e: Exception) {
        null
    }
}
