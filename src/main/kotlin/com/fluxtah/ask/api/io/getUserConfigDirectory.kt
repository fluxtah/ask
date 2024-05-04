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