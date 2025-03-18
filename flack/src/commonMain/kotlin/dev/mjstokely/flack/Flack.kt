package dev.mjstokely.flack


@Suppress("FunctionName")
expect fun Flack(): FlackConnection

/**
 * Main entry point for the Flack library.
 * This class provides a platform-independent API for connecting to and monitoring WiFi networks.
 */
//expect class Flack {
//    /**
//     * Flow of the current connection status.
//     * This flow emits a new [ConnectionStatus] when the status changes.
//     */
//    val connectionStatus: Flow<ConnectionStatus>
//
//    /**
//     * Connects to a WiFi network with the specified parameters.
//     *
//     * @param ssid The SSID (Service Set Identifier) of the WiFi network to connect to.
//     * @param password The password for the WiFi network. Can be null for open networks.
//     * @param isTemporary Whether the connection should be temporary.
//     * On Android: determines if the connection persists beyond the app's lifecycle.
//     * On iOS: maps directly to the joinOnce property of NEHotspotConfiguration.
//     * @return A [Result] indicating the success or failure of the connection attempt.
//     */
//    suspend fun connect(ssid: String, password: String?, isTemporary: Boolean = false): Result<Unit>
//
//    /**
//     * Disconnects from the current WiFi network, if connected.
//     *
//     * @return A [Result] indicating the success or failure of the disconnection attempt.
//     */
//    suspend fun disconnect(): Result<Unit>
//
//    companion object {
//        /**
//         * Creates a new instance of Flack.
//         * On Android, this requires a Context parameter.
//         */
//        fun create(): Flack
//    }
//}