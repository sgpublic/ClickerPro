package io.github.clickerpro.service

import android.accessibilityservice.AccessibilityService
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.provider.Settings
import android.view.accessibility.AccessibilityEvent
import androidx.lifecycle.MutableLiveData
import io.github.clickerpro.Application
import io.github.clickerpro.BuildConfig
import io.github.clickerpro.base.BaseOverlayWidget
import io.github.clickerpro.core.manager.ConfigManager
import io.github.clickerpro.core.manager.RuntimeValues
import io.github.clickerpro.core.util.Orientation
import io.github.clickerpro.core.util.log
import io.github.clickerpro.ui.overlay.BiliConneSwitcherOverlay
import java.lang.ref.WeakReference

class ClickerAccessibilityService: AccessibilityService() {
    private var Switcher: BiliConneSwitcherOverlay? = null

    override fun onCreate() {
        if (ConfigManager.MODE != ConfigManager.Mode.ACCESSIBILITY) {
            log.warn("当前非无障碍模式")
            return
        }
        log.info("onCreate")
        instance = WeakReference(this)
        registerReceiver(receiver, IntentFilter(Intent.ACTION_CONFIGURATION_CHANGED))
    }

    @Suppress("KotlinConstantConditions")
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (Orientation.CURRENT != Orientation.LANDSCAPE) {
                Switcher?.hide()
            } else if (BuildConfig.BUILD_TYPE == BuildConfig.TYPE_SNAPSHOT
                || BuildConfig.BUILD_TYPE == BuildConfig.TYPE_DEBUG) {
                createSwitcherOverlay()
            }
        }
    }

    override fun onServiceConnected() {
        log.info("onServiceConnected")
        STARTED.postValue(true)
    }

    private var canCreate = false
    private fun createOverlay() {
        canCreate = true
    }

    private fun destroyOverlay() {
        Switcher?.destroy()
        Switcher = null
    }

    private val types: Map<Int, String> = hashMapOf<Int, String>().also {
        AccessibilityEvent::class.java.fields.filter {
            return@filter it.name.startsWith("TYPE_")
        }.forEach { field ->
            it[field.getInt(null)] = field.name
        }
    }
    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        log.debug("onAccessibilityEvent\n" +
                "  eventType: ${types[event.eventType] ?: "TYPE_UNKNOWN"}\n" +
                "  packageName: ${event.packageName}")
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            if ("com.bilibili.priconne" == event.packageName) {
                createSwitcherOverlay()
            }
        }
    }

    private fun createSwitcherOverlay() {
        if (canCreate && Switcher == null) {
            Switcher = BaseOverlayWidget.create(Application.APPLICATION_CONTEXT)
            Switcher?.create()
        }
        if (RuntimeValues.OVERLAY_OPEN) {
            Switcher?.show()
        }
    }

    override fun onInterrupt() {
        log.info("onInterrupt")
    }

    override fun onDestroy() {
        log.info("onDestroy")
        unregisterReceiver(receiver)
        STARTED.postValue(false)
        destroyOverlay()
        instance = null
    }

    companion object {
        private var instance: WeakReference<ClickerAccessibilityService>? = null

        fun getInstance(): ClickerAccessibilityService? {
            return instance?.get()
        }

        val STARTED: MutableLiveData<Boolean> = MutableLiveData(false)

        fun createOverlay() {
            if (Settings.canDrawOverlays(Application.APPLICATION_CONTEXT)) {
                getInstance()?.createOverlay()
            }
        }

        fun destroyOverlay() {
            if (Settings.canDrawOverlays(Application.APPLICATION_CONTEXT)) {
                getInstance()?.destroyOverlay()
            }
        }
    }
}
