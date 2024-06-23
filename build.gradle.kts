import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.*

val ktor_version: String by project

val versionProps = Properties().apply {
    load(file("version.properties").inputStream())
}
val appVersion = versionProps["version"].toString()

plugins {
    kotlin("jvm") version "1.9.10"
    application
    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.21"
    id("com.github.johnrengelman.shadow") version "7.1.0"
}

group = "com.fluxtah.ask"
version = appVersion

repositories {
    mavenCentral()
    maven { url = uri("https://repo.gradle.org/gradle/libs-releases") }
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("com.github.fluxtah:ask-plugin-sdk:0.7.2")
    implementation("com.fluxtah.ask:ask-plugin-koder:0.7.2")

    implementation(project(":ask-api"))

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    implementation("org.gradle:gradle-tooling-api:8.4")
    implementation("org.jetbrains.kotlin:kotlin-compiler-embeddable:1.7.0")

    implementation("org.jline:jline:3.25.0")

    implementation("org.jetbrains.exposed:exposed-dao:0.37.3")

    implementation("ch.qos.logback:logback-classic:1.4.12")

    implementation("io.insert-koin:koin-core:3.5.6")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

application {
    mainClass.set("MainKt")
}


tasks.shadowJar {
    archiveClassifier.set("")
    configurations = listOf(project.configurations.runtimeClasspath.get())
    destinationDirectory.set(file("$buildDir/libs"))
}

tasks.register("packageDistribution") {
    dependsOn("shadowJar")
    doLast {
        // Directory where everything will be packaged
        val distDir = file("$buildDir/dist")

        // Ensure directory exists
        distDir.mkdirs()

        copy {
            from("$buildDir/libs")
            from("scripts")  // Assumes 'scripts' is at the project root
            into(distDir)
        }

        // Define the path for the tarball within the dist directory
        val tarPath = "$distDir/ask-$appVersion.tar.gz"

        // Create tarball containing all contents of the dist directory
        exec {
            commandLine("tar", "czvf", tarPath, "-C", distDir.path, ".")
        }

        // Log the output path for verification
        println("Distribution package created at: $tarPath")

        exec {
            println("SHA256 checksum:")
            commandLine("shasum", "-a", "256", tarPath)
        }
    }
}

tasks.register("generateVersionFile") {
    doLast {
        val fileContent = """
            package com.fluxtah.ask

            object Version {
                const val APP_VERSION = "$appVersion"
            }
        """.trimIndent()
        val file = File("src/main/kotlin/com/fluxtah/ask/Version.kt")
        file.parentFile.mkdirs()
        file.writeText(fileContent)
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    dependsOn("generateVersionFile")
}