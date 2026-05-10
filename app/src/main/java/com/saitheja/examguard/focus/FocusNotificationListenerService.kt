package com.saitheja.examguard.focus

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

class FocusNotificationListenerService : NotificationListenerService() {
    private lateinit var store: FocusSettingsStore

    override fun onCreate() {
        super.onCreate()
        store = FocusSettingsStore(this)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val whitelist = store.loadWhitelistedPackages()
        if (!FocusPolicy.isAllowedPackage(sbn.packageName, whitelist)) {
            cancelNotification(sbn.key)
        }
    }
}
