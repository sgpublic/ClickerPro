package io.github.clickerpro.core.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory


private val loggers: HashMap<Class<*>, Logger> = hashMapOf()

val Any.log: Logger get() = if (loggers[javaClass] != null) { loggers[javaClass]!! } else {
    LoggerFactory.getLogger(this::class.isCompanion.take(javaClass.enclosingClass, javaClass))
}

class Slf4j {
    companion object {
        fun clear() {
            loggers.clear()
        }
    }
}