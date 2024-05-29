package com.fluxtah.ask

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class VersionUtilsTest {
    @Test
    fun `test isVersionGreater`() {
        assertTrue(VersionUtils.isVersionGreater("0.1.1", "0.1.0"))
        assertFalse(VersionUtils.isVersionGreater("0.1.0", "0.1.1"))
        assertTrue(VersionUtils.isVersionGreater("1.0.0", "0.9.9"))
        assertFalse(VersionUtils.isVersionGreater("1.0.0", "1.0.0"))
        assertTrue(VersionUtils.isVersionGreater("1.0.10", "1.0.2"))
        assertFalse(VersionUtils.isVersionGreater("1.0.2", "1.0.10"))
    }
}