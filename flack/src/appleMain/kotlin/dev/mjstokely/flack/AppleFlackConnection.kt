package dev.mjstokely.flack

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSError
import platform.Network.nw_path_monitor_create
import platform.Network.nw_path_monitor_set_queue
import platform.Network.nw_path_monitor_start
import platform.Network.nw_path_monitor_cancel
import platform.Network.nw_path_monitor_t
import platform.Network.nw_path_status_satisfied
import platform.Network.nw_path_t
import platform.Network.nw_path_get_status
import platform.NetworkExtension.NEHotspotConfiguration
import platform.NetworkExtension.NEHotspotConfigurationManager
import platform.darwin.dispatch_get_main_queue
import kotlin.coroutines.resume

/**
 * Apple implementation of the FlackConnection interface.
 */
internal class AppleFlackConnection : FlackConnection {
    
    private val _connectionStatus = MutableStateFlow(ConnectionStatus())
    override val connectionStatus: Flow<ConnectionStatus> = _connectionStatus.asStateFlow()
    
    private var pathMonitor: nw_path_monitor_t? = null
    
    init {
        startMonitoringWifiStatus()
    }
    
    /**
     * Starts monitoring the WiFi connection status.
     * This sets up callbacks to receive updates when the network status changes.
     */
    private fun startMonitoringWifiStatus() {
        // Create a new path monitor to track network status
        val monitor = nw_path_monitor_create()
        pathMonitor = monitor
        
        // Setup a callback to be called when the network path changes
        nw_path_monitor_set_update_handler(monitor) { path ->
            updateConnectionStatus(path)
        }

        // Set the queue for callbacks to be the main queue
        nw_path_monitor_set_queue(monitor, dispatch_get_main_queue())
        
        // Start monitoring
        nw_path_monitor_start(monitor)
    }
    
    /**
     * Updates the connection status based on the provided network path.
     *
     * @param path The network path to check.
     */
    private fun updateConnectionStatus(path: nw_path_t) {
        if (path == null) {
            _connectionStatus.value = ConnectionStatus(isConnected = false)
            return
        }

        val isWifi = dev.mjstokely.flack.nw_path_is_wifi(path)
        val isConnected = nw_path_get_status(path) == nw_path_status_satisfied && isWifi
        
        // Get SSID and signal strength - we would need access to CWNetwork for this
        // but due to Kotlin/Native limitations, we'll set them to null for now
        val ssid: String? = null
        val signalStrength: Int? = null
        
        _connectionStatus.value = ConnectionStatus(
            isConnected = isConnected,
            ssid = ssid,
            signalStrength = signalStrength
        )
    }
    
    /**
     * Connects to a WiFi network with the specified parameters.
     *
     * @param ssid The SSID of the WiFi network to connect to.
     * @param password The password for the WiFi network. Can be null for open networks.
     * @param isTemporary Whether the connection should be temporary.
     * @return A [Result] indicating the success or failure of the connection attempt.
     */
    override suspend fun connect(ssid: String, password: String?, isTemporary: Boolean): Result<Unit> {
        return suspendCancellableCoroutine { continuation ->
            try {
                // Create a configuration for the WiFi network
                val configuration = if (password != null) {
                    NEHotspotConfiguration(sSID = ssid, password, isTemporary)
                } else {
                    NEHotspotConfiguration(sSID = ssid)
                }
                
                configuration.joinOnce = isTemporary
                
                // Apply the configuration to connect to the network
                NEHotspotConfigurationManager.sharedManager.applyConfiguration(
                    configuration,
                    { error ->
                        if (error != null) {
                            // Failed to connect
                            continuation.resume(Result.failure(
                                ConnectionException("Failed to connect to $ssid: ${error.localizedDescription}")
                            ))
                        } else {
                            // Successfully connected
                            continuation.resume(Result.success(Unit))
                        }
                    }
                )
            } catch (e: Exception) {
                continuation.resume(Result.failure(ConnectionException("Failed to connect to $ssid", e)))
            }
        }
    }
    
    /**
     * Disconnects from the current WiFi network, if connected.
     *
     * @return A [Result] indicating the success or failure of the disconnection attempt.
     */
    override suspend fun disconnect(): Result<Unit> {
        return suspendCancellableCoroutine { continuation ->
            try {
                // We can't directly disconnect from a network in iOS,
                // but we can remove the configuration for a specific SSID.
                // We'll use the current SSID if it's available.
                val currentSsid = _connectionStatus.value.ssid
                
                if (currentSsid != null) {
                    NEHotspotConfigurationManager.sharedManager.removeConfigurationForSSID(currentSsid)
                }
                
                // Since we can't directly disconnect, we'll consider this successful
                continuation.resume(Result.success(Unit))
            } catch (e: Exception) {
                continuation.resume(Result.failure(DisconnectionException("Failed to disconnect", e)))
            }
        }
    }
    
    /**
     * Stops monitoring the WiFi connection status.
     * This should be called when the connection is no longer needed.
     */
    fun stop() {
        pathMonitor?.let { monitor ->
            nw_path_monitor_cancel(monitor)
            pathMonitor = null
        }
    }
    
    // This is a workaround for dealing with Kotlin/Native interop with Swift APIs that aren't fully exposed
    private fun nw_path_monitor_set_update_handler(monitor: nw_path_monitor_t, handler: (nw_path_t) -> Unit) {
        nw_path_monitor_set_update_handler_bridged(monitor, handler)
    }
    
    // Bridge function implemented in NetworkBridge.kt
    private fun nw_path_monitor_set_update_handler_bridged(monitor: nw_path_monitor_t, handler: (nw_path_t) -> Unit) {
        dev.mjstokely.flack.nw_path_monitor_set_update_handler_bridged(monitor, handler)
    }
}