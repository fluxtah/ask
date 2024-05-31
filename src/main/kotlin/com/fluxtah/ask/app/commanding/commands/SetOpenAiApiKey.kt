/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.app.commanding.commands

import com.fluxtah.ask.api.store.user.UserProperties

class SetOpenAiApiKey(private val userProperties: UserProperties, private val apiKey: String) : Command() {
    override val requiresApiKey: Boolean = false
    override suspend fun execute() {
        userProperties.setOpenAiApiKey(apiKey)
        userProperties.save()
    }
}