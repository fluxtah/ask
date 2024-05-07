plugins {
    kotlin("jvm") version "1.9.23"
    id("com.github.johnrengelman.shadow") version "7.1.0"
    id("com.github.fluxtah.ask-gradle-plugin") version "0.3.0"
}

group = "com.fluxtah"
version = "1.0"

repositories {
    mavenLocal()
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("com.github.fluxtah:ask-plugin-sdk:0.3.0")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}