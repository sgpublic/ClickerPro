package io.github.clickerpro.core.logback

import ch.qos.logback.classic.Level
import io.github.clickerpro.BuildConfig
import io.github.clickerpro.core.util.take

class ConsoleFilter: ClickerLevelFilter(
    BuildConfig.DEBUG.take(Level.INFO, Level.INFO)
)