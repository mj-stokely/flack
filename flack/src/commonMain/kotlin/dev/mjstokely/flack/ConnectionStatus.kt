package dev.mjstokely.flack

/**
 * Represents the status of a WiFi connection.
 *
 * @property isConnected Whether the device is currently connected to a WiFi network.
 * @property ssid The SSID (Service Set Identifier) of the connected WiFi network, or null if not connected.
 * @property signalStrength The signal strength of the connected WiFi network as a percentage (0-100), or null if not available.
 */
data class ConnectionStatus(
    val isConnected: Boolean = false,
    val ssid: String? = null,
    val signalStrength: Int? = null
)