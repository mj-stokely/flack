package dev.mjstokely.flack

import android.content.Context

/**
 * A singleton to hold the application context for Flack library on Android.
 */
object AndroidContext {
    private var applicationContext: Context? = null

    /**
     * Initialize the FlackContext with the application context.
     * Should be called early in the application lifecycle.
     *
     * @param context The application context.
     */
    fun initialize(context: Context) {
        applicationContext = context.applicationContext
    }

    /**
     * Get the application context.
     * Throws an IllegalStateException if initialize() has not been called.
     *
     * @return The application context.
     */
    fun getApplicationContext(): Context {
        return applicationContext ?: throw IllegalStateException(
            "FlackContext has not been initialized. Call initialize() first."
        )
    }
}