package com.fluxtah.ask.api.plugins

import com.fluxtah.ask.api.io.getUserConfigDirectory
import com.fluxtah.askpluginsdk.AskPlugin
import com.fluxtah.askpluginsdk.AssistantDefinition
import java.io.File
import java.net.URLClassLoader
import java.util.*

class AskPluginLoader {
    fun loadPlugins() : List<AssistantDefinition> {
        val plugins = mutableListOf<AssistantDefinition>()
        val pluginsDir = File(getUserConfigDirectory(),"plugins")
        if (!pluginsDir.exists()) {
            pluginsDir.mkdirs()
        }
        val urls = pluginsDir.listFiles { file -> file.path.endsWith(".jar") }?.map { it.toURI().toURL() }?.toTypedArray()
        val classLoader = URLClassLoader(urls, Thread.currentThread().contextClassLoader)

        val services = ServiceLoader.load(AskPlugin::class.java, classLoader)
        for (plugin in services) {
            plugin.createAssistantDefinition()?.let {
                plugins.add(it)
            }
        }

        return plugins
    }
}