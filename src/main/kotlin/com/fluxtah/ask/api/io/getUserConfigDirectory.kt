/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.api.io

import java.io.File

fun getUserConfigDirectory(): File {
        val userHome = System.getProperty("user.home")
        val configDir = File(userHome, ".ask")

        if (!configDir.exists()) {
            configDir.mkdir()
        }

        return configDir
    }