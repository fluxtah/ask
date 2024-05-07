plugins {
    kotlin("jvm") version "1.9.23"
    id("com.github.johnrengelman.shadow") version "7.1.0"
    id("com.github.fluxtah.ask-gradle-plugin") version "0.2.0"
}

group = "com.fluxtah"
version = "1.0"

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("com.github.fluxtah:ask-plugin-sdk:0.2.0")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}