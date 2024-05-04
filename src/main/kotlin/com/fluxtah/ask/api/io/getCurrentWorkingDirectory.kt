package com.fluxtah.ask.api.io

fun getCurrentWorkingDirectory(): String {
    return System.getProperty("user.dir")
}