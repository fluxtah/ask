/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.assistants

import com.fluxtah.askpluginsdk.AssistantDefinition
import com.fluxtah.askpluginsdk.Fun
import com.fluxtah.askpluginsdk.FunParam
import com.fluxtah.askpluginsdk.logging.AskLogger
import kotlinx.serialization.Serializable

class FoodOrderingAssistant(logger: AskLogger) : AssistantDefinition(
    logger = logger,
    id = "food",
    name = "Food Ordering Assistant",
    description = "An assistant that helps you order food from your favorite restaurants.",
    version = "1.0.0",
    instructions = "Help the user order food from their favorite restaurants.",
    model = "gpt-3.5-turbo",
    temperature = 0.7f,
    functions = FoodOrderAssistantFunctions()
)

@Serializable
data class Query(
    val restaurant: String = "",
    val food: String = "",
    val quantity: Int = 0
)

class FoodOrderAssistantFunctions {
    @Fun("order food by query")
    fun orderFood(
        @FunParam("The food ordering query")
        query: Query
    ) : String {
        return "Ordering ${query.quantity} ${query.food} from ${query.restaurant}."
    }
}