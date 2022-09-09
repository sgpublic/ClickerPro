package io.github.clickerpro.core.wrapper

import android.content.Context
import android.hardware.input.InputManager
import android.os.SystemClock
import android.view.InputEvent
import android.view.MotionEvent
import io.github.clickerpro.core.util.log
import java.lang.reflect.Method

class InputManagerWrapper private constructor(private val iim: InputManager){
    private val injectInputEvent: Method = InputManager::class.java.getMethod(
        "injectInputEvent",
        InputEvent::class.java, Int::class.java
    )

    fun injectTap(x: Int, y: Int, time: Int = 10) {
        val now = SystemClock.uptimeMillis()
        injectInputEvent.invoke(iim, MotionEvent.obtain(
            now, now, MotionEvent.ACTION_DOWN,
            x.toFloat(), y.toFloat(), 0
        ), 0)
        injectInputEvent.invoke(iim, MotionEvent.obtain(
            now + time, now + time,
            MotionEvent.ACTION_UP,
            x.toFloat(), y.toFloat(), 0
        ), 0)
    }

    companion object {
        private var imw: InputManagerWrapper? = null

        fun getInstance(context: Context): InputManagerWrapper? {
            if (imw == null) {
                try {
                    val clazz = InputManager::class.java
                    imw = InputManagerWrapper(context.getSystemService(clazz))
                } catch (e: Exception) {
                    log.error("获取 InputManager 失败", e)
                }
            }
            return imw
        }
    }
}