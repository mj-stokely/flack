package dev.mjstokely.flackexample

/**
 * Platform interface for handling platform-specific functionality.
 */
interface Platform {
    val name: String
}

/**
 * Creates a platform-specific implementation.
 */
expect fun getPlatform(): Platform