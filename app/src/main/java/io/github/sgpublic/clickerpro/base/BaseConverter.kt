package io.github.sgpublic.clickerpro.base

interface BaseConverter<T, V> {
    fun encode(obj: T): V
    fun decode(value: V): T
}