package com.example.sleepenvoirmenttracker

import android.os.Bundle
import androidx.compose.material3.*
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.sleepenvoirmenttracker.ui.theme.SleepEnvoirmentTrackerTheme

class MainActivity : ComponentActivity() {
    private lateinit var lightSensorManager: LightSensorManager
    private lateinit var noiseMonitor: NoiseMonitor
    private lateinit var repository: SleepRepository

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted! You can log this or update UI
            android.util.Log.d("Permissions", "Microphone access granted")
        } else {
            // Permission denied. You should explain to the user why the app needs it.
            android.util.Log.e("Permissions", "Microphone access denied")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ???
        repository = SleepRepository(this)
        requestPermissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
        // Why is the context this?
        val lightSensorManager = LightSensorManager(this, repository)
        val noiseMonitor = NoiseMonitor(this)
//        enableEdgeToEdge()
        setContent {
            SleepEnvoirmentTrackerTheme {
                HomeScreen(repository, lightSensorManager, noiseMonitor)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        noiseMonitor.stop()
        lightSensorManager.stop()
    }
}

