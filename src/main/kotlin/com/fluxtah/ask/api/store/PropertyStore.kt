package com.fluxtah.ask.api.store

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*

class PropertyStore(private val filename: String) {
    private val properties = Properties()

    fun getUserConfigDirectory(): File {
        val userHome = System.getProperty("user.home")
        val configDir = File(userHome, ".ask")

        if (!configDir.exists()) {
            configDir.mkdir()
        }

        return configDir
    }

    init {
        load()
    }

    @Synchronized
    fun setProperty(key: String, value: String) {
        properties.setProperty(key, value)
        save()
    }

    @Synchronized
    fun getProperty(key: String, defaultValue: String = ""): String {
        return properties.getProperty(key, defaultValue)
    }

    fun load() {
        val file = File(getUserConfigDirectory(), filename)
        if (file.exists()) {
            FileInputStream(file).use { properties.load(it) }
        }
    }

    fun save() {
        FileOutputStream(filename).use { properties.store(it, null) }
    }
}