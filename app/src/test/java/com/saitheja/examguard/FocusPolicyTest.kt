package com.saitheja.examguard

import com.saitheja.examguard.focus.FocusPolicy
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FocusPolicyTest {
    @Test
    fun shouldAllowWhitelistedApps() {
        assertTrue(FocusPolicy.isAllowedPackage("com.example.textbook", setOf("com.example.textbook")))
    }

    @Test
    fun shouldBlockNonWhitelistedWhenStrictModeEnabled() {
        assertTrue(FocusPolicy.shouldBlockPackage("com.social.media", true, setOf("com.example.textbook")))
    }

    @Test
    fun shouldNotBlockWhenStrictModeDisabled() {
        assertFalse(FocusPolicy.shouldBlockPackage("com.social.media", false, emptySet()))
    }
}
