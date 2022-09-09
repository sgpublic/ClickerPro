package io.github.clickerpro.core.logback

import android.util.Log
import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.filter.Filter
import ch.qos.logback.core.spi.FilterReply
import io.github.clickerpro.core.util.take

open class ClickerLevelFilter @JvmOverloads constructor(
    var level: Level = Level.INFO
): Filter<ILoggingEvent>() {
    override fun start() {
        Log.e(".core.logback.ClickerLevelFilter (ClickerLevelFilter.kt:18)", "setLevel(${level})")
        super.start()
    }

    override fun decide(event: ILoggingEvent): FilterReply {
        return event.level.isGreaterOrEqual(level).take(FilterReply.NEUTRAL, FilterReply.DENY)
    }
}