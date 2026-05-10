package com.saitheja.examguard.focus

object FocusPolicy {
    fun isAllowedPackage(packageName: String, whitelist: Set<String>): Boolean {
        return packageName in whitelist || packageName.startsWith("com.android")
    }

    fun shouldBlockPackage(packageName: String, strictModeEnabled: Boolean, whitelist: Set<String>): Boolean {
        if (!strictModeEnabled) return false
        return !isAllowedPackage(packageName, whitelist)
    }
}
