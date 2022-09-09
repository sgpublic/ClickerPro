package io.github.clickerpro.core.util

fun String.hashEquals(value: String): Boolean {
    return hashCode() == value.hashCode()
}