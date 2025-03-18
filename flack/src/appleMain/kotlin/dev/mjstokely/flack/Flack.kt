package dev.mjstokely.flack

@Suppress("FunctionName")
actual fun Flack(): FlackConnection = AppleFlackConnection()

/**
 * Apple implementation of the Flack class.
 */
//actual class Flack private constructor(private val connection: FlackConnection) {
//    /**
//     * Flow of the current connection status.
//     */
//    actual val connectionStatus: Flow<ConnectionStatus> = connection.connectionStatus
//
//    /**
//     * Connects to a WiFi network with the specified parameters.
//     *
//     * @param ssid The SSID of the WiFi network to connect to.
//     * @param password The password for the WiFi network. Can be null for open networks.
//     * @param isTemporary Whether the connection should be temporary.
//     * @return A [Result] indicating the success or failure of the connection attempt.
//     */
//    actual suspend fun connect(ssid: String, password: String?, isTemporary: Boolean): Result<Unit> {
//        return connection.connect(ssid, password, isTemporary)
//    }
//
//    /**
//     * Disconnects from the current WiFi network, if connected.
//     *
//     * @return A [Result] indicating the success or failure of the disconnection attempt.
//     */
//    actual suspend fun disconnect(): Result<Unit> {
//        return connection.disconnect()
//    }
//
//    actual companion object {
//        /**
//         * Creates a new instance of Flack.
//         *
//         * @return A new Flack instance.
//         */
//        actual fun create(): Flack {
//            val connection = AppleFlackConnection()
//            return Flack(connection)
//        }
//    }
//}