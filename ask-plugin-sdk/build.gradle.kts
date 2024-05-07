import java.util.*

plugins {
    kotlin("jvm")
    id("maven-publish")
}

val versionProps = Properties().apply {
    load(file("../version.properties").inputStream())
}
val appVersion = versionProps["version"].toString()

group = "com.github.fluxtah"
version = appVersion

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
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