/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.askpluginsdk

abstract class AssistantDefinition(
    val id: String,
    val name: String,
    val model: String,
    val temperature: Float,
    val description: String,
    val version: String,
    val instructions: String,
    val functions: Any
)

