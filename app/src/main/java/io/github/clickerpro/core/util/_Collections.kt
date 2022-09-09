package io.github.clickerpro.core.util

fun <T: Any?> (() -> T).repeatToList(size: Int): ArrayList<T> {
    return ArrayList<T>(size).also {
        for (i in 0 until size) {
            it.add(this.invoke())
        }
    }
}