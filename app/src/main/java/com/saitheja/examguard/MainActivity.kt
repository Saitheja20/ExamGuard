package com.saitheja.examguard

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.saitheja.examguard.focus.EmergencyCallManager
import com.saitheja.examguard.focus.FocusSettingsStore
import com.saitheja.examguard.ui.FocusAppPickerView

class MainActivity : ComponentActivity() {
    private lateinit var store: FocusSettingsStore
    private lateinit var emergencyCallManager: EmergencyCallManager

    private val requestPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        store = FocusSettingsStore(this)
        emergencyCallManager = EmergencyCallManager(this, store)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                FocusDashboard(
                    initialStrictMode = store.isStrictModeEnabled(),
                    initialWhitelist = store.loadWhitelistedPackages(),
                    initialEmergencyContacts = store.loadEmergencyContacts(),
                    onStrictModeChanged = {
                        store.setStrictModeEnabled(it)
                        if (it) emergencyCallManager.start() else emergencyCallManager.stop()
                    },
                    onWhitelistChanged = { store.saveWhitelistedPackages(it) },
                    onEmergencyContactsChanged = { store.saveEmergencyContacts(it) },
                    onExitRequested = {
                        store.setStrictModeEnabled(false)
                        emergencyCallManager.stop()
                        finishAffinity()
                    }
                )
            }
        }
    }
}

@Composable
private fun FocusDashboard(
    initialStrictMode: Boolean,
    initialWhitelist: Set<String>,
    initialEmergencyContacts: Set<String>,
    onStrictModeChanged: (Boolean) -> Unit,
    onWhitelistChanged: (Set<String>) -> Unit,
    onEmergencyContactsChanged: (Set<String>) -> Unit,
    onExitRequested: () -> Unit
) {
    val context = LocalContext.current
    var strictMode by remember { mutableStateOf(initialStrictMode) }
    var selected by remember { mutableStateOf(initialWhitelist) }
    var contactsText by remember { mutableStateOf(initialEmergencyContacts.joinToString(",")) }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("ExamGuard Focus", style = MaterialTheme.typography.headlineSmall)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Strict Mode")
                Switch(
                    checked = strictMode,
                    onCheckedChange = {
                        strictMode = it
                        onStrictModeChanged(it)
                        Toast.makeText(context, if (it) "Strict Mode ON" else "Strict Mode OFF", Toast.LENGTH_SHORT).show()
                    }
                )
            }
            Text("Study App Whitelist (PackageManager + RecyclerView)")
            TextField(
                value = contactsText,
                onValueChange = {
                    contactsText = it
                    onEmergencyContactsChanged(
                        it.split(",")
                            .map(String::trim)
                            .filter(String::isNotBlank)
                            .toSet()
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Emergency Contacts (comma separated numbers)") }
            )
            Column(modifier = Modifier.weight(1f)) {
                FocusAppPickerView(
                    context = context,
                    selectedPackages = selected,
                    onSelectionChanged = {
                        selected = it
                        onWhitelistChanged(it)
                    }
                )
            }
            Button(onClick = onExitRequested, modifier = Modifier.fillMaxWidth()) {
                Text("Exit App")
            }
        }
    }
}
