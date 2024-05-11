/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.api.tools.fn

import com.fluxtah.ask.api.clients.openai.assistants.model.AssistantRunStepDetails.ToolCalls.ToolCallDetails.FunctionToolCallDetails
import kotlinx.serialization.json.*
import kotlin.reflect.KFunction
import kotlin.reflect.KType
import kotlin.reflect.full.memberFunctions

class FunctionInvoker {
    fun <T : Any> invokeFunction(targetInstance: T, callDetails: FunctionToolCallDetails): String {
        // Find the function in the target instance's class by name
        val function = targetInstance::class.memberFunctions.find { it.name == callDetails.function.name }
            ?: throw IllegalArgumentException("Function not found: ${callDetails.function.name}")

        // Deserialize the JSON argument into a generic map
        try {
            val argsMap = Json.decodeFromString<Map<String, JsonElement>>(callDetails.function.arguments.replace("\\$", "$"))

            // Prepare arguments for the function call
            val args = prepareArguments(function, argsMap)

            // Invoke the function with the prepared arguments
            val result = function.call(targetInstance, *args)

            // Ensure the result is a String
            return result.toString()
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

            // Deserialize the argument to the expected type of the parameter
            deserializeArgument(jsonElement, parameter.type)
        }.toTypedArray()
    }

    private fun deserializeArgument(jsonElement: JsonElement, type: KType): Any? {
        return when (type.classifier) {
            String::class -> jsonElement.jsonPrimitive.content
            Int::class -> jsonElement.jsonPrimitive.int
            Boolean::class -> jsonElement.jsonPrimitive.boolean
            // Add more types as needed
            else -> throw IllegalArgumentException("Unsupported parameter type: $type")
        }
    }
}
