package io.github.clickerpro.core.logback

import android.util.Log
import ch.qos.logback.classic.Level
import io.github.clickerpro.BuildConfig
import io.github.clickerpro.core.util.take

class ConsoleFilter: ClickerLevelFilter(
    BuildConfig.DEBUG.take(Level.INFO, Level.INFO)
) {
    override fun start() {
        Log.e(".core.logback.ConsoleFilter (ConsoleFilter.kt:12)", "setLevel(${level})")
        super.start()
    }
}