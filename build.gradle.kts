import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val ktor_version: String by project

plugins {
    kotlin("jvm") version "1.9.10"
    application
    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.21"
    id("com.github.johnrengelman.shadow") version "7.1.0"
}

group = "com.fluxtah"
version = "0.1"

repositories {
    mavenCentral()
    maven { url = uri("https://repo.gradle.org/gradle/libs-releases") }
}

dependencies {
    // SLF4J API
    implementation("org.slf4j:slf4j-api:1.7.32")
    // Logback (which includes the SLF4J binding)
    implementation("ch.qos.logback:logback-classic:1.4.12")

    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("io.ktor:ktor-client-okhttp:$ktor_version")
    implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktor_version")
    implementation("io.ktor:ktor-client-logging-jvm:$ktor_version")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.gradle:gradle-tooling-api:8.4")
    implementation("org.jetbrains.kotlin:kotlin-compiler-embeddable:1.7.0")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
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
        val tarPath = "$distDir/ask-0.1.tar.gz"

        // Create tarball containing all contents of the dist directory
        exec {
            commandLine("tar", "czvf", tarPath, "-C", distDir.path, ".")
        }

        // Log the output path for verification
        println("Distribution package created at: $tarPath")
    }
}