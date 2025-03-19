package dev.mjstokely.flackexample

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.mjstokely.flack.ConnectionStatus
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    val viewModel = remember { WiFiViewModel() }
    val uiState by viewModel.uiState.collectAsState()
    
    MaterialTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Flack WiFi Demo") }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Connection Status Card
                ConnectionStatusCard(
                    connectionStatus = uiState.connectionStatus,
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Connection Form
                ConnectionForm(
                    ssid = uiState.ssidInput,
                    onSsidChange = viewModel::updateSsid,
                    password = uiState.passwordInput,
                    onPasswordChange = viewModel::updatePassword,
                    isTemporary = uiState.isTemporary,
                    onTemporaryChange = viewModel::updateTemporary,
                    isConnecting = uiState.isConnecting,
                    onConnectClick = viewModel::connect,
                    isDisconnecting = uiState.isDisconnecting,
                    onDisconnectClick = viewModel::disconnect,
                    isConnected = uiState.connectionStatus.isConnected,
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Messages
                uiState.errorMessage?.let { errorMsg ->
                    ErrorMessage(
                        message = errorMsg,
                        onDismiss = viewModel::clearError,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                
                uiState.connectionMessage?.let { connMsg ->
                    SuccessMessage(
                        message = connMsg,
                        onDismiss = viewModel::clearConnectionMessage,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                Text(
                    text = "Flack: Kotlin Multiplatform WiFi Connection Library",
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
fun ConnectionStatusCard(
    connectionStatus: ConnectionStatus,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Connection Status",
                style = MaterialTheme.typography.h6
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Status:")
                Text(
                    text = if (connectionStatus.isConnected) "Connected" else "Disconnected",
                    fontWeight = FontWeight.Bold,
                    color = if (connectionStatus.isConnected) 
                        MaterialTheme.colors.primary 
                    else 
                        MaterialTheme.colors.error
                )
            }
            
            connectionStatus.ssid?.let { networkSsid ->
                if (connectionStatus.isConnected) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Network:")
                        Text(
                            text = networkSsid,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            connectionStatus.signalStrength?.let { signal ->
                if (connectionStatus.isConnected) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Signal Strength:")
                        Text(
                            text = "$signal%",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ConnectionForm(
    ssid: String,
    onSsidChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    isTemporary: Boolean,
    onTemporaryChange: (Boolean) -> Unit,
    isConnecting: Boolean,
    onConnectClick: () -> Unit,
    isDisconnecting: Boolean,
    onDisconnectClick: () -> Unit,
    isConnected: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Connect to WiFi",
                style = MaterialTheme.typography.h6
            )
            
            OutlinedTextField(
                value = ssid,
                onValueChange = onSsidChange,
                label = { Text("SSID (Network Name)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = { Text("Password (leave empty for open networks)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = isTemporary,
                    onCheckedChange = onTemporaryChange
                )
                Text("Temporary Connection")
            }
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = onConnectClick,
                    enabled = !isConnecting && !isDisconnecting && ssid.isNotBlank(),
                    modifier = Modifier.weight(1f)
                ) {
                    if (isConnecting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Connect")
                    }
                }
                
                Button(
                    onClick = onDisconnectClick,
                    enabled = !isConnecting && !isDisconnecting && isConnected,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.error
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    if (isDisconnecting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Disconnect")
                    }
                }
            }
        }
    }
}

@Composable
fun ErrorMessage(
    message: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        backgroundColor = MaterialTheme.colors.error.copy(alpha = 0.1f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = message,
                color = MaterialTheme.colors.error,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onDismiss) {
                Text("×", style = MaterialTheme.typography.h6)
            }
        }
    }
}

@Composable
fun SuccessMessage(
    message: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        backgroundColor = MaterialTheme.colors.primary.copy(alpha = 0.1f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = message,
                color = MaterialTheme.colors.primary,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onDismiss) {
                Text("×", style = MaterialTheme.typography.h6)
            }
        }
    }
}