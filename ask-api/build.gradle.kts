import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val ktor_version: String by project

plugins {
    kotlin("jvm") version "1.9.10"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.21"
}

group = "com.fluxtah.ask"
version = "0.5.0"

repositories {
    mavenCentral()
    maven { url = uri("https://repo.gradle.org/gradle/libs-releases") }
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    api("com.github.fluxtah:ask-plugin-sdk:0.7.2")

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

    implementation("org.xerial:sqlite-jdbc:3.41.2.2")
    implementation("org.jetbrains.exposed:exposed-core:0.37.3")
    implementation("org.jetbrains.exposed:exposed-dao:0.37.3")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.37.3")
    implementation("org.jetbrains.exposed:exposed-java-time:0.37.3")

    implementation("io.insert-koin:koin-core:3.5.6")

    testImplementation(kotlin("test"))

    // MockK for mocking
    testImplementation("io.mockk:mockk:1.12.0")

    // Coroutine Testing
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.5.2")

    // JUnit
    testImplementation("junit:junit:4.13.2")
}


tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

tasks.test {
    useJUnitPlatform()
}
