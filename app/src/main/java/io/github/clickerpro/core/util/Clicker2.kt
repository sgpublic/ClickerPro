package io.github.clickerpro.core.util

/** jni 实现 */

@Deprecated("Under normal circumstances, you cannot use the input command to click across processes")
object Clicker2 {
    init {
        System.loadLibrary("clickerpro")
    }

    external fun startClick(x: Int, y: Int, id: String, root: Boolean = false)
    external fun stopClick(id: String)
    external fun clear()
}