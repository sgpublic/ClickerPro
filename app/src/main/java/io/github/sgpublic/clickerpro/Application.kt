package io.github.sgpublic.clickerpro

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.content.res.Configuration
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.joran.JoranConfigurator
import ch.qos.logback.core.joran.spi.JoranException
import ch.qos.logback.core.util.StatusPrinter
import io.github.sgpublic.clickerpro.core.util.log
import org.slf4j.LoggerFactory
import java.io.IOException

class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        application = this
        loadLogbackConfig()
        log.info("APP启动")
    }

    private fun loadLogbackConfig() {
        val context = LoggerFactory.getILoggerFactory() as LoggerContext
        val configurator = JoranConfigurator()
        configurator.context = context
        context.reset()
        try {
            configurator.doConfigure(resources.assets.open("logback-clickerpro.xml"))
            StatusPrinter.printInCaseOfErrorsOrWarnings(context)
        } catch (e: JoranException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    companion object {
        private lateinit var application: Application

        val APPLICATION_CONTEXT: Context get() = application.applicationContext
        val CONTENT_RESOLVER: ContentResolver get() = APPLICATION_CONTEXT.contentResolver

        val IS_NIGHT_MODE: Boolean get() = APPLICATION_CONTEXT.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

        fun onToast(context: AppCompatActivity, content: String?) {
            context.runOnUiThread {
                Toast.makeText(APPLICATION_CONTEXT, content, Toast.LENGTH_SHORT).show()
            }
        }
        fun onToast(context: AppCompatActivity, @StringRes content: Int) {
            onToast(context, APPLICATION_CONTEXT.resources.getText(content).toString())
        }
        fun onToast(context: AppCompatActivity, @StringRes content: Int, code: Int) {
            val contentShow = (APPLICATION_CONTEXT.resources.getText(content).toString() + "($code)")
            onToast(context, contentShow)
        }
        fun onToast(context: AppCompatActivity, @StringRes content: Int, message: String?, code: Int) {
            if (message != null) {
                val contentShow = APPLICATION_CONTEXT.resources.getText(content).toString() + "，$message($code)"
                onToast(context, contentShow)
            } else {
                onToast(context, content, code)
            }
        }

        fun getString(@StringRes textId: Int, vararg arg: Any): String {
            return APPLICATION_CONTEXT.resources.getString(textId, *arg)
        }

        fun getColor(@ColorRes colorId: Int): Int {
            return APPLICATION_CONTEXT.getColor(colorId)
        }
    }
}
