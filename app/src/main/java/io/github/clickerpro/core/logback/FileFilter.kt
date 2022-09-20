package io.github.clickerpro.core.logback

import ch.qos.logback.classic.Level
import io.github.clickerpro.BuildConfig
import io.github.clickerpro.core.util.take

class FileFilter: ClickerLevelFilter(
    (BuildConfig.BUILD_TYPE == BuildConfig.TYPE_SNAPSHOT).take(Level.DEBUG, Level.INFO)
)