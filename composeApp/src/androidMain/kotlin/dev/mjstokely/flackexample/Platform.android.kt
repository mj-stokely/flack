package dev.mjstokely.flackexample

import android.os.Build
import dev.mjstokely.flack.Flack
import dev.mjstokely.flack.FlackConnection
import dev.mjstokely.flack.AndroidContext

object AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform