pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        mavenCentral() // Optional, add if your plugin or other dependencies might be there
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
rootProject.name = "hello"

