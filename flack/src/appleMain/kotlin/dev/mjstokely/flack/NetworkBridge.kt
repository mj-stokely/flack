package dev.mjstokely.flack

import kotlinx.cinterop.*
import platform.Network.*
import platform.darwin.*

/**
 * Bridge implementations for Network framework functions.
 * These are used to interface with the iOS Network framework.
 */
@OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
internal fun nw_path_monitor_set_update_handler_bridged(
    monitor: nw_path_monitor_t, 
    handler: (nw_path_t) -> Unit
) {
    // Set the update handler
    nw_path_monitor_set_update_handler(monitor) { path ->
        if (path != null) {
            handler(path)
        }
    }
    
    // Set queue to main dispatch queue
    nw_path_monitor_set_queue(monitor, dispatch_get_main_queue())
}

/**
 * Checks if the given network path is using WiFi.
 */
@OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
internal fun nw_path_is_wifi(path: nw_path_t): Boolean {
    // Check if path is using WiFi interface type (1 is WiFi)
    // WiFi interface type is defined as 1 in Network framework
    return nw_path_uses_interface_type(path, 1u)
}