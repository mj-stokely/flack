package dev.mjstokely.flackexample

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.mjstokely.flack.ConnectionStatus
import dev.mjstokely.flack.Flack
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for managing WiFi connections using the Flack library.
 */
class WiFiViewModel : ViewModel() {
    // Use platform-specific Flack instance creation
    private val flack = Flack()
    
    private val _uiState = MutableStateFlow(WiFiUIState())
    val uiState: StateFlow<WiFiUIState> = _uiState.asStateFlow()

    init {
        // Collect connection status updates
        viewModelScope.launch {
            flack.connectionStatus.collect { connectionStatus ->
                _uiState.update { currentState ->
                    currentState.copy(
                        connectionStatus = connectionStatus,
                        isConnecting = false,
                        isDisconnecting = false
                    )
                }
            }
        }
    }
    
    /**
     * Updates the SSID input field.
     */
    fun updateSsid(ssid: String) {
        _uiState.update { it.copy(ssidInput = ssid) }
    }
    
    /**
     * Updates the password input field.
     */
    fun updatePassword(password: String) {
        _uiState.update { it.copy(passwordInput = password) }
    }
    
    /**
     * Updates the temporary connection option.
     */
    fun updateTemporary(isTemporary: Boolean) {
        _uiState.update { it.copy(isTemporary = isTemporary) }
    }
    
    /**
     * Attempts to connect to the WiFi network with the provided credentials.
     */
    fun connect() {
        val ssid = _uiState.value.ssidInput
        if (ssid.isBlank()) {
            _uiState.update { it.copy(errorMessage = "SSID cannot be empty") }
            return
        }
        
        _uiState.update { 
            it.copy(
                isConnecting = true, 
                errorMessage = null,
                connectionMessage = "Connecting to $ssid..."
            ) 
        }
        
        viewModelScope.launch {
            val result = flack.connect(
                ssid = ssid,
                password = _uiState.value.passwordInput.takeIf { it.isNotBlank() },
                isTemporary = _uiState.value.isTemporary
            )
            
            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            connectionMessage = "Successfully connected to $ssid",
                            errorMessage = null
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isConnecting = false,
                            errorMessage = "Connection failed: ${error.message}",
                            connectionMessage = null
                        )
                    }
                }
            )
        }
    }
    
    /**
     * Disconnects from the current WiFi network.
     */
    fun disconnect() {
        _uiState.update { 
            it.copy(
                isDisconnecting = true,
                errorMessage = null,
                connectionMessage = "Disconnecting..."
            ) 
        }
        
        viewModelScope.launch {
            val result = flack.disconnect()
            
            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            connectionMessage = "Successfully disconnected",
                            errorMessage = null
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isDisconnecting = false,
                            errorMessage = "Disconnect failed: ${error.message}",
                            connectionMessage = null
                        )
                    }
                }
            )
        }
    }
    
    /**
     * Clears any error messages.
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
    
    /**
     * Clears the connection message.
     */
    fun clearConnectionMessage() {
        _uiState.update { it.copy(connectionMessage = null) }
    }
}

/**
 * Data class representing the UI state for the WiFi connection screen.
 */
data class WiFiUIState(
    val ssidInput: String = "",
    val passwordInput: String = "",
    val isTemporary: Boolean = true,
    val isConnecting: Boolean = false,
    val isDisconnecting: Boolean = false,
    val connectionStatus: ConnectionStatus = ConnectionStatus(),
    val errorMessage: String? = null,
    val connectionMessage: String? = null
)