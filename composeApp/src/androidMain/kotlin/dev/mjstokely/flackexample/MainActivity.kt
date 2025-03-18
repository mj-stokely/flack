package dev.mjstokely.flackexample

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import dev.mjstokely.flack.AndroidContext

class MainActivity : ComponentActivity() {
    // Set up permission request launcher
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.entries.all { it.value }) {
            // All permissions granted, proceed with initialization
            setContentWithPermissions()
        } else {
            // Handle permission denial
            setContent {
                PermissionDeniedScreen()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check for required permissions
        checkRequiredPermissions()
    }

    private fun checkRequiredPermissions() {
        val requiredPermissions = mutableListOf<String>().apply {
            add(Manifest.permission.ACCESS_FINE_LOCATION)
            add(Manifest.permission.ACCESS_WIFI_STATE)
            add(Manifest.permission.CHANGE_WIFI_STATE)

            // For Android 13+ we need these additional permissions
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(Manifest.permission.NEARBY_WIFI_DEVICES)
            }
        }.toTypedArray()

        val allPermissionsGranted = requiredPermissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }

        if (allPermissionsGranted) {
            setContentWithPermissions()
        } else {
            requestPermissionLauncher.launch(requiredPermissions)
        }
    }

    private fun setContentWithPermissions() {
        // Create custom application context to provide to Flack
        AndroidContext.initialize(applicationContext)

        setContent {
            App()
        }
    }
}

@Composable
fun PermissionDeniedScreen() {
    // Simple screen showing that WiFi permissions are required
    Surface {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            Text("WiFi permissions are required for this app to function.")
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}