plugins {
    kotlin("jvm")
}

group = "com.fluxtah.ask"
version = "0.12"

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