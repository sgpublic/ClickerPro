package io.github.clickerpro.core.manager

import android.content.Context
import android.content.SharedPreferences
import io.github.clickerpro.Application
import io.github.clickerpro.core.util.Clicker

object ConfigManager {
    private const val KEY_MODE = "mode"
    var MODE: Mode get() = try {
        Mode.valueOf(getString(KEY_MODE, Mode.DEFAULT_NAME))
    } catch (e: Exception) { Mode.DEFAULT }
    set(value) { putString(KEY_MODE, value.name) }
    enum class Mode {
        ACCESSIBILITY, ROOT;
        companion object {
            val DEFAULT: Mode = ACCESSIBILITY
            val DEFAULT_NAME: String = ACCESSIBILITY.name
        }
    }

    private const val KEY_ROOT = "root"
    var ROOT: Boolean get() = getBoolean(KEY_ROOT, false)
    set(value) {
        Clicker.Root.AVAILABLE.postValue(value)
        putBoolean(KEY_ROOT, value)
    }


    private fun getString(key: String, defValue: String = "") =
        sharedPreferences.getString(key, defValue).toString()
    private fun getInt(key: String, defValue: Int = 0) =
        sharedPreferences.getInt(key, defValue)
    private fun getLong(key: String, defValue: Long = 0L) =
        sharedPreferences.getLong(key, defValue)
    private fun getBoolean(key: String, defValue: Boolean = false) =
        sharedPreferences.getBoolean(key, defValue)

    private fun putString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }
    private fun putInt(key: String, value: Int) {
        sharedPreferences.edit().putInt(key, value).apply()
    }
    private fun putLong(key: String, value: Long) {
        sharedPreferences.edit().putLong(key, value).apply()
    }
    private fun putBoolean(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    private val sharedPreferences: SharedPreferences
        get() = Application.APPLICATION_CONTEXT
        .getSharedPreferences("user", Context.MODE_PRIVATE)
        ?: throw NullPointerException("SharedPreferences 'user' not found")
}