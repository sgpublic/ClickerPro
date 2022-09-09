package io.github.clickerpro.core.util

fun <R> exWhile(func: () -> R, check: (R) -> Boolean, callback: (R) -> Boolean) {
    var value = func.invoke()
    while (check.invoke(value)) {
        if (!callback.invoke(value)) {
            break
        }
        value = func.invoke()
    }
}