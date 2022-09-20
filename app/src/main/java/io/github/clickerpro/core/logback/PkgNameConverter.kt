package io.github.clickerpro.core.logback

import ch.qos.logback.classic.pattern.ClassicConverter
import ch.qos.logback.classic.spi.ILoggingEvent
import io.github.clickerpro.BuildConfig

class PkgNameConverter : ClassicConverter() {
    override fun convert(event: ILoggingEvent): String {
        var loggerName = event.loggerName
        if (loggerName.startsWith(BuildConfig.APPLICATION_ID)) {
            loggerName = loggerName.substring(BuildConfig.APPLICATION_ID.length)
        }
        return loggerName
    }
}