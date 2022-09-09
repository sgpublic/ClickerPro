package io.github.clickerpro.core.util

import android.util.TypedValue
import io.github.clickerpro.Application

val Int.dp: Int get() = toFloat().dp
val Int.dpFloat: Float get() = toFloat().dpFloat
val Float.dp: Int get() = dpFloat.toInt()
val Float.dpFloat: Float get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this,
    Application.APPLICATION_CONTEXT.resources.displayMetrics)

val Int.sp: Int get() = toFloat().sp
val Int.spFloat: Float get() = toFloat().spFloat
val Float.sp: Int get() = spFloat.toInt()
val Float.spFloat: Float get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this,
    Application.APPLICATION_CONTEXT.resources.displayMetrics)