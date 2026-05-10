package com.saitheja.examguard.focus

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import com.saitheja.examguard.MainActivity

class FocusAccessibilityService : AccessibilityService() {
    private lateinit var store: FocusSettingsStore

    override fun onServiceConnected() {
        super.onServiceConnected()
        store = FocusSettingsStore(this)
        serviceInfo = serviceInfo.apply {
            eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val packageName = event?.packageName?.toString() ?: return
        val shouldBlock = FocusPolicy.shouldBlockPackage(
            packageName = packageName,
            strictModeEnabled = store.isStrictModeEnabled(),
            whitelist = store.loadWhitelistedPackages()
        )
        if (shouldBlock) {
            performGlobalAction(GLOBAL_ACTION_HOME)
            startActivity(
                Intent(this, MainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            )
        }
    }

    override fun onInterrupt() = Unit
}
