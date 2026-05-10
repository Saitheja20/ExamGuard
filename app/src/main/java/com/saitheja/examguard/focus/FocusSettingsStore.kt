package com.saitheja.examguard.focus

import android.content.Context

class FocusSettingsStore(context: Context) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveWhitelistedPackages(packages: Set<String>) {
        prefs.edit().putStringSet(KEY_WHITELIST, packages).apply()
    }

    fun loadWhitelistedPackages(): Set<String> = prefs.getStringSet(KEY_WHITELIST, emptySet()) ?: emptySet()

    fun saveEmergencyContacts(contacts: Set<String>) {
        prefs.edit().putStringSet(KEY_EMERGENCY_CONTACTS, contacts).apply()
    }

    fun loadEmergencyContacts(): Set<String> = prefs.getStringSet(KEY_EMERGENCY_CONTACTS, emptySet()) ?: emptySet()

    fun setStrictModeEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_STRICT_MODE, enabled).apply()
    }

    fun isStrictModeEnabled(): Boolean = prefs.getBoolean(KEY_STRICT_MODE, false)

    companion object {
        private const val PREFS_NAME = "focus_settings"
        private const val KEY_WHITELIST = "whitelisted_packages"
        private const val KEY_EMERGENCY_CONTACTS = "emergency_contacts"
        private const val KEY_STRICT_MODE = "strict_mode"
    }
}
