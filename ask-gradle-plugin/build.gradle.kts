import java.util.*

plugins {
    application
    kotlin("jvm") version "1.9.23"
    id("com.github.johnrengelman.shadow") version "7.1.0" apply false
    id("java-gradle-plugin")
    id("maven-publish")
}

val versionProps = Properties().apply {
    load(file("../version.properties").inputStream())
}
val appVersion = versionProps["version"].toString()

group = "com.github.fluxtah"
version = appVersion

repositories {
    mavenLocal()
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    testImplementation(kotlin("test"))
}

gradlePlugin {
    plugins {
        create("askGradlePlugin") {
            id = "com.github.fluxtah.ask-gradle-plugin"
            implementationClass = "com.fluxtah.ask.AskGradlePlugin"
        }
    }
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
    repositories {
        maven {
            url = uri("https://jitpack.io")
        }
    }
}
