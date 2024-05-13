plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
rootProject.name = "ask"
includeBuild("../ask-plugin-sdk")
includeBuild("../ask-plugin-koder")
include("ask-api")
