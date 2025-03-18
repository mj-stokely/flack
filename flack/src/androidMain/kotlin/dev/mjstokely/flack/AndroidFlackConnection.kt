package dev.mjstokely.flack

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSpecifier
import android.os.Build
import androidx.core.content.getSystemService
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Android implementation of the FlackConnection interface.
 *
 * @param context The Android application context.
 */
internal class AndroidFlackConnection(private val context: Context) : FlackConnection {

    private val connectivityManager: ConnectivityManager? = 
        context.getSystemService()
    
    private val wifiManager: WifiManager? = 
        context.applicationContext.getSystemService()
    
    private val _connectionStatus = MutableStateFlow(ConnectionStatus())
    
    override val connectionStatus: Flow<ConnectionStatus> = _connectionStatus.asStateFlow()
    
    private var currentNetwork: Network? = null
    private var networkCallback: ConnectivityManager.NetworkCallback? = null

    init {
        startMonitoringWifiStatus()
    }

    /**
     * Starts monitoring the WiFi connection status.
     * This sets up callbacks to receive updates when the network status changes.
     */
    private fun startMonitoringWifiStatus() {
        if (connectivityManager == null) {
            _connectionStatus.value = ConnectionStatus(isConnected = false)
            return
        }

        // Create a network callback to monitor network changes
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                updateConnectionStatus(network, true)
            }

            override fun onLost(network: Network) {
                if (network == currentNetwork) {
                    currentNetwork = null
                    _connectionStatus.value = ConnectionStatus(isConnected = false)
                }
            }

            override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
                if (network == currentNetwork) {
                    updateConnectionStatus(network, true)
                }
            }
        }

        // Register the callback for WiFi networks
        val request = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()
        
        connectivityManager.registerNetworkCallback(request, callback)
        networkCallback = callback

        // Check the current WiFi connection status
        updateCurrentConnectionStatus()
    }

    /**
     * Updates the current connection status based on the active network.
     */
    private fun updateCurrentConnectionStatus() {
        if (connectivityManager == null) return

        val activeNetwork = connectivityManager.activeNetwork ?: return
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return
        
        if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
            updateConnectionStatus(activeNetwork, true)
        } else {
            _connectionStatus.value = ConnectionStatus(isConnected = false)
        }
    }

    /**
     * Updates the connection status for a specific network.
     *
     * @param network The network to update the status for.
     * @param isConnected Whether the device is connected to this network.
     */
    private fun updateConnectionStatus(network: Network, isConnected: Boolean) {
        if (!isConnected) {
            _connectionStatus.value = ConnectionStatus(isConnected = false)
            return
        }

        currentNetwork = network
        
        // Get SSID of the connected network
        val ssid = getConnectedWifiSsid()
        
        // Get signal strength if available
        val signalStrength = getSignalStrength()
        
        _connectionStatus.value = ConnectionStatus(
            isConnected = true,
            ssid = ssid,
            signalStrength = signalStrength
        )
    }

    /**
     * Gets the SSID of the currently connected WiFi network.
     *
     * @return The SSID of the connected network, or null if not connected or unable to determine.
     */
    private fun getConnectedWifiSsid(): String? {
        if (wifiManager == null) return null
        
        val connectionInfo = wifiManager.connectionInfo ?: return null
        var ssid = connectionInfo.ssid
        
        // WifiManager returns SSIDs wrapped in double quotes
        if (ssid.startsWith("\"") && ssid.endsWith("\"")) {
            ssid = ssid.substring(1, ssid.length - 1)
        }
        
        return if (ssid == "<unknown ssid>") null else ssid
    }

    /**
     * Gets the signal strength of the currently connected WiFi network.
     *
     * @return The signal strength as a percentage (0-100), or null if not available.
     */
    private fun getSignalStrength(): Int? {
        if (wifiManager == null) return null
        
        val connectionInfo = wifiManager.connectionInfo ?: return null
        val rssi = connectionInfo.rssi
        
        // Convert RSSI to percentage
        return if (rssi <= -100) {
            0
        } else if (rssi >= -50) {
            100
        } else {
            2 * (rssi + 100)
        }
    }

    /**
     * Connects to a WiFi network with the specified parameters.
     *
     * @param ssid The SSID of the WiFi network to connect to.
     * @param password The password for the WiFi network. Can be null for open networks.
     * @param isTemporary Whether the connection should persist beyond the app's lifecycle.
     * @return A [Result] indicating the success or failure of the connection attempt.
     */
    override suspend fun connect(ssid: String, password: String?, isTemporary: Boolean): Result<Unit> {
        return try {
            if (connectivityManager == null) {
                return Result.failure(ConnectionException("ConnectivityManager not available"))
            }

            // Create a WiFi network specifier
            val specifierBuilder = WifiNetworkSpecifier.Builder()
                .setSsid(ssid)
            
            // Set the password if provided (otherwise it's an open network)
            if (password != null) {
                specifierBuilder.setWpa2Passphrase(password)
            }
            
            val specifier = specifierBuilder.build()
            
            // Create a network request
            val request = NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .setNetworkSpecifier(specifier)
                .build()
            
            // Request the network connection
            suspendCancellableCoroutine { continuation ->
                // Create a callback to handle the connection result
                val callback = object : ConnectivityManager.NetworkCallback() {
                    override fun onAvailable(network: Network) {
                        // Lock this network for our app if not temporary
                        if (!isTemporary) {
                            connectivityManager.bindProcessToNetwork(network)
                        }
                        
                        currentNetwork = network
                        networkCallback?.let { connectivityManager.unregisterNetworkCallback(it) }
                        networkCallback = this
                        
                        // Update the connection status
                        updateConnectionStatus(network, true)
                        
                        // Complete the coroutine
                        if (continuation.isActive) {
                            continuation.resume(Result.success(Unit))
                        }
                    }
                    
                    override fun onUnavailable() {
                        if (continuation.isActive) {
                            continuation.resume(Result.failure(ConnectionException("Failed to connect to $ssid")))
                        }
                    }
                }
                
                // Register the callback and request the network
                connectivityManager.requestNetwork(request, callback)
                
                // Make sure we unregister the callback if the coroutine is cancelled
                continuation.invokeOnCancellation {
                    connectivityManager.unregisterNetworkCallback(callback)
                }
            }
            
        } catch (e: Exception) {
            Result.failure(ConnectionException("Failed to connect to $ssid", e))
        }
    }

    /**
     * Disconnects from the current WiFi network, if connected.
     *
     * @return A [Result] indicating the success or failure of the disconnection attempt.
     */
    override suspend fun disconnect(): Result<Unit> {
        return try {
            if (connectivityManager == null) {
                return Result.failure(DisconnectionException("ConnectivityManager not available"))
            }
            
            // Unbind from any network
            connectivityManager.bindProcessToNetwork(null)
            
            // Unregister the current network callback if it exists
            networkCallback?.let {
                connectivityManager.unregisterNetworkCallback(it)
                networkCallback = null
            }
            
            // Start monitoring again to track future network changes
            startMonitoringWifiStatus()
            
            // Update the connection status
            _connectionStatus.value = ConnectionStatus(isConnected = false)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(DisconnectionException("Failed to disconnect", e))
        }
    }
}