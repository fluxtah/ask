package com.fluxtah.ask.api.plugins

import com.fluxtah.askpluginsdk.AskPlugin
import com.fluxtah.askpluginsdk.AssistantDefinition
import com.fluxtah.askpluginsdk.CreateAssistantDefinitionsConfig
import com.fluxtah.askpluginsdk.io.getUserConfigDirectory
import com.fluxtah.askpluginsdk.logging.AskLogger
import java.io.File
import java.net.URLClassLoader
import java.util.*

class AskPluginLoader(private val logger: AskLogger) {
    fun loadPlugins(): List<AssistantDefinition> {
        val plugins = mutableListOf<AssistantDefinition>()
        val pluginsDir = File(getUserConfigDirectory(), "plugins")
        if (!pluginsDir.exists()) {
            pluginsDir.mkdirs()
        }
        val urls =
            pluginsDir.listFiles { file -> file.path.endsWith(".jar") }?.map { it.toURI().toURL() }?.toTypedArray()
        val classLoader = URLClassLoader(urls, Thread.currentThread().contextClassLoader)

        val services = ServiceLoader.load(AskPlugin::class.java, classLoader)
        for (plugin in services) {
            plugin.createAssistantDefinitions(CreateAssistantDefinitionsConfig(logger)).forEach {
                plugins.add(it)
            }
        }

        return plugins
    }

    fun loadPlugin(file: File): AssistantDefinition {
        val plugins = mutableListOf<AssistantDefinition>()
        val urls = listOf(file).map { it.toURI().toURL() }.toTypedArray()
        val classLoader = URLClassLoader(urls, Thread.currentThread().contextClassLoader)

        val services = ServiceLoader.load(AskPlugin::class.java, classLoader)
        for (plugin in services) {
            plugin.createAssistantDefinitions(CreateAssistantDefinitionsConfig(logger)).forEach {
                plugins.add(it)
            }
        }

        return plugins.first()
    }
}