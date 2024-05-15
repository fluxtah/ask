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
    instructions = "Help the user order food, nothing else.",
    model = "gpt-4o",
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

    @Fun("gets a list of food vendors by category")
    fun getFoodVendors(
        @FunParam("The category of food for instance chinese, indian, fast food, etc.")
        category: String
    ) : String {
        return """
            Nearby:
            1. McDonald's - Fast food
            2. KFC - Fried chicken
            3. Pizza Hut - Pizza
            4. Subway - Sandwiches
            5. Starbucks - Coffee
            
            Open later:
            6. Taco Bell - Mexican
            7. Dunkin' - Donuts
            8. Indian Palace - Indian
            9. Panda Express - Chinese
            10. Chipotle - Mexican
            11. Olive Garden - Italian
            12. Red Lobster - Seafood
        """.trimIndent()
    }
}