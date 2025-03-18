package dev.mjstokely.flack

/**
 * Base exception class for Flack library errors.
 */
open class FlackException(message: String, cause: Throwable? = null) : Exception(message, cause)

/**
 * Exception thrown when a connection attempt fails.
 */
class ConnectionException(message: String, cause: Throwable? = null) : FlackException(message, cause)

/**
 * Exception thrown when attempting to disconnect fails.
 */
class DisconnectionException(message: String, cause: Throwable? = null) : FlackException(message, cause)

/**
 * Exception thrown when a required permission is missing.
 */
class PermissionException(message: String) : FlackException(message)

/**
 * Exception thrown when a required feature is not supported on the current platform.
 */
class UnsupportedFeatureException(message: String) : FlackException(message)