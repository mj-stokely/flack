package dev.mjstokely.flackexample

import android.app.Application
import dev.mjstokely.flack.AndroidContext

class FlackExampleApp: Application() {

    override fun onCreate() {
        super.onCreate()
        AndroidContext.initialize(this)
    }
}