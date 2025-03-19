package dev.mjstokely.flack

import kotlinx.coroutines.flow.Flow


internal expect fun FlackConnection(): FlackConnection

/**
 * Main entry point for the Flack library.
 * This class provides a platform-independent API for connecting to and monitoring WiFi networks.
 */
class Flack private constructor(private val connection: FlackConnection) {
    /**
     * Flow of the current connection status.
     */
    val connectionStatus: Flow<ConnectionStatus> = connection.connectionStatus

    /**
     * Connects to a WiFi network with the specified parameters.
     *
     * @param ssid The SSID of the WiFi network to connect to.
     * @param password The password for the WiFi network. Can be null for open networks.
     * @param isTemporary Whether the connection should be temporary.
     * @return A [Result] indicating the success or failure of the connection attempt.
     */
    suspend fun connect(ssid: String, password: String?, isTemporary: Boolean): Result<Unit> {
        return connection.connect(ssid, password, isTemporary)
    }

    /**
     * Disconnects from the current WiFi network, if connected.
     *
     * @return A [Result] indicating the success or failure of the disconnection attempt.
     */
    suspend fun disconnect(): Result<Unit> {
        return connection.disconnect()
    }

    companion object {
        /**
         * Creates a new instance of Flack.
         *
         * @return A new Flack instance.
         */
        fun create(): Flack {
            val connection = FlackConnection()
            return Flack(connection)
        }
    }
}