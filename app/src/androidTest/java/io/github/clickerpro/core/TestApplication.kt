package io.github.clickerpro.core

import android.app.Application
import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry

class TestApplication: Application() {
    companion object {
        val CONTEXT: Context get() = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext
    }
}