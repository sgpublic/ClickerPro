package io.github.clickerpro.core.util

import android.accessibilityservice.GestureDescription
import android.graphics.Path
import androidx.annotation.IntRange
import androidx.lifecycle.MutableLiveData
import io.github.clickerpro.BuildConfig
import io.github.clickerpro.core.manager.ConfigManager
import io.github.clickerpro.service.ClickerAccessibilityService
import okhttp3.internal.closeQuietly
import okio.use
import java.io.BufferedReader
import java.io.Closeable
import java.io.File
import java.io.PrintWriter
import java.nio.charset.StandardCharsets
import java.util.concurrent.ConcurrentHashMap
import java.util.regex.Pattern

object Clicker {
    private val tasks: ConcurrentHashMap<Int, Point> = ConcurrentHashMap()
//    private val inputManager: InputManagerWrapper? = InputManagerWrapper
//        .getInstance(Application.APPLICATION_CONTEXT).also {
//            if (it == null) log.error("无法启动点击，请将设备信息报告给开发者")
//        }
    private var thread: Thread? = null

    fun startClick(point: Point, @IntRange(from = 0, to = 4) id: Int) {
        if (id < 0 || id > 4) { return }
        val service = ClickerAccessibilityService.getInstance() ?: return
        tasks[id] = point
        if (thread != null) { return }
        thread = ClickerThread(service).also {
            it.start()
        }
    }

    private class ClickerThread(
        private val service: ClickerAccessibilityService
    ): Thread() {
//        private val handler: Handler
//        init {
//            if (Looper.myLooper() == null) {
//                Looper.prepare()
//            }
//            handler = Handler(Looper.myLooper()!!)
//        }
//
//        private val lopper = Thread {
//            while (true) {
//                Looper.loop()
//            }
//        }

        override fun run() {
            try {
                while (true) {
                    for (i in 0 until 5) {
                        service.dispatchGesture(
                            tasks[i]?.gesture ?: continue,
                            null, null
                        )
                        sleep(40)
                    }
                }
            } catch (e: InterruptedException) {
                // 忽略线程打断报错
            } catch (e: Exception) {
                log.error("模拟持续点击失败", e)
            }
        }

//        override fun interrupt() {
//            Looper.myLooper()?.quitSafely()
//            lopper.interrupt()
//            super.interrupt()
//        }
    }


    fun stopClick(@IntRange(from = 0, to = 4) id: Int) {
        tasks.remove(id)
        if (tasks.isEmpty()) {
            thread?.interrupt()
            thread = null
        }
        log.debug("停止自动点击：<$id>")
    }

    fun clear() {
        thread?.interrupt()
        thread = null
        tasks.clear()
    }

    data class Point(
        val x: Int, val y: Int,
    ) {
        private val path: Path get() {
            return Path().also {
                val newPoint = randomized
                it.moveTo(newPoint.x.toFloat(), newPoint.y.toFloat(),)
            }
        }

        val randomized: Point get() = Point(
            x + RANDOM_RANGE.random(),
            y + RANDOM_RANGE.random()
        )

        val gesture: GestureDescription get() {
            return GestureDescription.Builder()
                .addStroke(GestureDescription.StrokeDescription(
                    path, 0, 5
                ))
                .build()
        }

        override fun toString(): String {
            return "Point(x: $x, y: $y)"
        }

        companion object {
            val RANDOM_RANGE = -5 .. 5
        }
    }

    @Deprecated("需要root")
    class TouchListener(private val listener: OnTouchListener) : Closeable {
        private val runtime: Runtime = Runtime.getRuntime()
        private var process: Process? = null
        private var input: BufferedReader? = null
        private var thread: Thread? = null

        fun start() {
            if (thread != null) {
                return
            }
            thread = Thread {
                process = runtime.exec("getevent -l /dev/input/event3")
                input = process!!.inputStream.bufferedReader(StandardCharsets.UTF_8)
                log.info("开始监听屏幕硬件输入")
                try {
                    var line = input?.readLine()?.split(SPLITTER)
                    while (line != null) {
                        log.debug("/dev/input/event3: $line")
                        if (line[1] == BTN_TOUCH && line[2] == DOWN) {
                            readUntilUp(input!!)
                        }
                        line = input?.readLine()?.split(SPLITTER)
                    }
                } catch (e: Exception) {
                    log.debug("监听意外停止", e)
                }
                log.debug("监听结束。\nerrorStream: ${process!!.errorStream.bufferedReader(StandardCharsets.UTF_8).readText()}")
                thread = null
            }.also {
                it.start()
            }
        }

        private fun readUntilUp(input: BufferedReader) {
            var x: Int? = null
            var y: Int? = null
            exWhile({ input.readLine().split(SPLITTER) }, { it[1] != BTN_TOUCH && it[2] != UP }) {
                if (it[1] == ABS_MT_POSITION_X) {
                    if (x != null) {
                        return@exWhile false
                    }
                    x = it[2].toIntOrNull(16) ?: -1
                } else if (it[1] == ABS_MT_POSITION_Y) {
                    if (y != null) {
                        return@exWhile false
                    }
                    y = it[2].toIntOrNull(16) ?: -1
                }
                return@exWhile true
            }
            if (x != null && y != null) {
                listener.onTouch(Point(x!!, y!!).also {
                    log.debug("onTouch $it")
                })
            }
        }

        override fun close() {
            thread.takeIf {
                it != null && !it.isInterrupted
            }?.interrupt()
            thread = null
            input?.closeQuietly()
            input = null
            process?.destroy()
            process = null
            log.info("停止监听屏幕硬件输入")
        }
    }

    private const val ABS_MT_POSITION_X = "ABS_MT_POSITION_X"
    private const val ABS_MT_POSITION_Y = "ABS_MT_POSITION_Y"
    private const val BTN_TOUCH = "BTN_TOUCH"
    private const val DOWN = "DOWN"
    private const val UP = "UP"
    private val SPLITTER = Pattern.compile("\\s+")

    interface OnTouchListener {
        fun onTouch(point: Point)
    }

    object Root {
        val AVAILABLE: MutableLiveData<Boolean> by lazy {
            MutableLiveData(ConfigManager.ROOT && request())
        }

        fun check(): Boolean {
            return File("/system/bin/su").exists() || File("/bin/su").exists()
        }

        fun request(): Boolean {
            val exec = Runtime.getRuntime().exec("su")
            PrintWriter(exec.outputStream).use {
                it.println("exit")
            }
            return exec.waitFor() == 0
        }

        fun openAccessibility(): Boolean {
            if (BuildConfig.BUILD_TYPE != BuildConfig.TYPE_DEBUG) {
                log.debug("当前非 debug 环境，跳过启动无障碍服务。")
                return false
            }
            val exec = Runtime.getRuntime().exec("su")
            PrintWriter(exec.outputStream).use {
                log.warn("正在使用 ROOT 自动启用无障碍服务！请勿在非 debug 环境使用此命令！")

//                val command0 = "settings put secure accessibility_enabled 0"
//                log.debug("- $command0")
//                it.println(command0)

//                val command1 = "settings delete secure enabled_accessibility_services"
//                log.debug("- $command1")
//                it.println(command1)
//
                val command2 = "settings put secure enabled_accessibility_services ${
                    BuildConfig.APPLICATION_ID
                }/${ClickerAccessibilityService::class.qualifiedName}"
                log.debug("- $command2")
                it.println(command2)

//                val command3 = "settings put secure accessibility_enabled 1"
//                log.debug("- $command3")
//                it.println(command3)

                it.println("exit")
            }
            return exec.waitFor() == 0
        }
    }
}