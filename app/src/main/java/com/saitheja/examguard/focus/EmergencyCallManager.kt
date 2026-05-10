package com.saitheja.examguard.focus

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.telecom.TelecomManager
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import androidx.core.content.ContextCompat

class EmergencyCallManager(
    private val context: Context,
    private val store: FocusSettingsStore
) {
    private val telephonyManager = context.getSystemService(TelephonyManager::class.java)
    private val telecomManager = context.getSystemService(TelecomManager::class.java)

    private val listener = object : PhoneStateListener() {
        override fun onCallStateChanged(state: Int, phoneNumber: String?) {
            if (state != TelephonyManager.CALL_STATE_RINGING) return
            if (!store.isStrictModeEnabled()) return
            val emergencyContacts = store.loadEmergencyContacts()
            val incoming = phoneNumber?.trim().orEmpty()
            if (incoming.isBlank()) return
            if (incoming !in emergencyContacts && canEndCall()) {
                telecomManager?.endCall()
            }
        }
    }

    fun start() {
        telephonyManager?.listen(listener, PhoneStateListener.LISTEN_CALL_STATE)
    }

    fun stop() {
        telephonyManager?.listen(listener, PhoneStateListener.LISTEN_NONE)
    }

    private fun canEndCall(): Boolean {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ANSWER_PHONE_CALLS) == PackageManager.PERMISSION_GRANTED
    }
}
