package io.github.clickerpro.core.util

import android.app.Activity
import android.content.Context
import android.view.Surface
import android.view.View
import android.view.WindowManager
import androidx.annotation.StringRes
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import io.github.clickerpro.Application

private val activities = ArrayList<Activity>()

fun Activity.register() {
    activities.add(this)
}

fun Activity.unregister() {
    activities.remove(this)
}

fun Context.finishAll() {
    for (activity in activities){
        if (!activity.isFinishing){
            activity.finish()
        }
    }
    activities.clear()
}

fun ViewBinding.makeSnack(@StringRes id: Int, duration: Int = Snackbar.LENGTH_SHORT, view: View = root) {
    Snackbar.make(view, id, duration).show()
}

enum class Orientation {
    PORTRAIT, LANDSCAPE, UNDEFINED;
    companion object {
        val CURRENT: Orientation get() {
            val rotation: Int = Application.getSystemService<WindowManager>()
                .defaultDisplay.rotation
            return when (rotation) {
                Surface.ROTATION_0, Surface.ROTATION_180 -> PORTRAIT
                Surface.ROTATION_90, Surface.ROTATION_270 -> LANDSCAPE
                else -> UNDEFINED
            }
        }

        fun wrappedContext(context: Context): Context {
            return context.createDisplayContext(
                Application.getSystemService<WindowManager>().defaultDisplay
            )
        }
    }
}