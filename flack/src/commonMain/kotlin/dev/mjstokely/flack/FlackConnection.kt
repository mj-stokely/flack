package dev.mjstokely.flack

import kotlinx.coroutines.flow.Flow

/**
 * Interface for handling WiFi connections.
 * This interface provides methods for connecting to WiFi networks, disconnecting, and observing the connection status.
 */
interface FlackConnection {
    /**
     * Flow of the current connection status.
     * This flow emits a new [ConnectionStatus] when the status changes.
     */
    val connectionStatus: Flow<ConnectionStatus>

    /**
     * Connects to a WiFi network with the specified parameters.
     *
     * @param ssid The SSID (Service Set Identifier) of the WiFi network to connect to.
     * @param password The password for the WiFi network. Can be null for open networks.
     * @param isTemporary Whether the connection should be temporary.
     * On Android: determines if the connection persists beyond the app's lifecycle.
     * On iOS: maps directly to the joinOnce property of NEHotspotConfiguration.
     * @return A [Result] indicating the success or failure of the connection attempt.
     */
    suspend fun connect(ssid: String, password: String?, isTemporary: Boolean = false): Result<Unit>

    /**
     * Disconnects from the current WiFi network, if connected.
     *
     * @return A [Result] indicating the success or failure of the disconnection attempt.
     */
    suspend fun disconnect(): Result<Unit>
}