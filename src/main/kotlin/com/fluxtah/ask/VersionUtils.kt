package com.fluxtah.ask

object VersionUtils {
    fun isVersionGreater(version1: String, version2: String): Boolean {
        val parts1 = version1.split(".")
        val parts2 = version2.split(".")

        // Assume all version strings have the same length and format
        for (i in parts1.indices) {
            val number1 = parts1[i].toInt()
            val number2 = parts2[i].toInt()
            if (number1 > number2) return true
            if (number1 < number2) return false
        }
        return false
    }
}