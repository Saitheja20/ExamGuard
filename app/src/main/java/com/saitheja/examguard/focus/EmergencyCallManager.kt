package com.saitheja.examguard.focus

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.telecom.TelecomManager
import android.telephony.PhoneStateListener
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager
import androidx.core.content.ContextCompat

class EmergencyCallManager(
    private val context: Context,
    private val store: FocusSettingsStore
) {
    private val telephonyManager = context.getSystemService(TelephonyManager::class.java)
    private val telecomManager = context.getSystemService(TelecomManager::class.java)
    private var latestIncomingNumber: String = ""
    private var receiverRegistered = false

    private val stateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action != TelephonyManager.ACTION_PHONE_STATE_CHANGED) return
            latestIncomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)?.trim().orEmpty()
        }
    }

    // Maintained for API 29-30 where TelephonyCallback is unavailable.
    @Suppress("DEPRECATION")
    private val listener = object : PhoneStateListener() {
        override fun onCallStateChanged(state: Int, phoneNumber: String?) {
            if (state != TelephonyManager.CALL_STATE_RINGING) return
            maybeBlockIncomingCall(phoneNumber?.trim().orEmpty().ifBlank { latestIncomingNumber })
        }
    }

    private val modernCallback: TelephonyCallback? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            object : TelephonyCallback(), TelephonyCallback.CallStateListener {
                override fun onCallStateChanged(state: Int) {
                    if (state != TelephonyManager.CALL_STATE_RINGING) return
                    maybeBlockIncomingCall(latestIncomingNumber)
                }
            }
        } else {
            null
        }

    @Suppress("DEPRECATION")
    fun start() {
        registerStateReceiver()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            telephonyManager?.registerTelephonyCallback(context.mainExecutor, modernCallback ?: return)
        } else {
            telephonyManager?.listen(listener, PhoneStateListener.LISTEN_CALL_STATE)
        }
    }

    @Suppress("DEPRECATION")
    fun stop() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            modernCallback?.let { telephonyManager?.unregisterTelephonyCallback(it) }
        } else {
            telephonyManager?.listen(listener, PhoneStateListener.LISTEN_NONE)
        }
        unregisterStateReceiver()
    }

    private fun maybeBlockIncomingCall(number: String) {
        if (!store.isStrictModeEnabled()) return
        val emergencyContacts = store.loadEmergencyContacts()
        if (number.isBlank()) return
        if (number !in emergencyContacts && canEndCall()) {
            telecomManager?.endCall()
        }
    }

    private fun canEndCall(): Boolean {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ANSWER_PHONE_CALLS) == PackageManager.PERMISSION_GRANTED
    }

    private fun registerStateReceiver() {
        if (receiverRegistered) return
        context.registerReceiver(stateReceiver, IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED))
        receiverRegistered = true
    }

    private fun unregisterStateReceiver() {
        if (!receiverRegistered) return
        context.unregisterReceiver(stateReceiver)
        receiverRegistered = false
    }
}
