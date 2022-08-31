package io.github.sgpublic.clickerpro.base

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewbinding.ViewBinding
import com.yanzhenjie.sofia.Sofia
import io.github.sgpublic.clickerpro.Application
import io.github.sgpublic.clickerpro.R
import io.github.sgpublic.clickerpro.core.util.finishAll
import io.github.sgpublic.clickerpro.core.util.register
import io.github.sgpublic.clickerpro.core.util.unregister

abstract class BaseActivity<VB : ViewBinding>: AppCompatActivity() {
    private var _binding: VB? = null
    @Suppress("PropertyName")
    protected val ViewBinding: VB get() = _binding!!

    private var rootViewBottom: Int = 0

    final override fun onCreate(savedInstanceState: Bundle?) {
        beforeCreate()
        super.onCreate(savedInstanceState)

        register()

        setupContentView()
        if (savedInstanceState != null) {
            STATE.putAll(savedInstanceState)
        }
        onViewSetup()
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (!applyBackPressed()) onBackPressedDispatcher.onBackPressed()
            }
        })
        onActivityCreated(savedInstanceState != null)
    }

    protected open fun beforeCreate() { }

    protected abstract fun onActivityCreated(hasSavedInstanceState: Boolean)

    private fun setupContentView() {
        _binding = onCreateViewBinding()
        setContentView(ViewBinding.root)

        Sofia.with(this)
            .statusBarBackgroundAlpha(0)
            .navigationBarBackgroundAlpha(0)
            .invasionNavigationBar()
            .invasionStatusBar()
            .statusBarDarkFont()
    }

    protected abstract fun onCreateViewBinding(): VB

    protected open fun initViewAtTop(view: View){
        var statusbarheight = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            statusbarheight = resources.getDimensionPixelSize(resourceId)
        }
        val params = view.layoutParams as ViewGroup.MarginLayoutParams
        params.topMargin = statusbarheight
    }

    protected open fun initViewAtBottom(view: View) {
        rootViewBottom = view.layoutParams.height
        ViewCompat.setOnApplyWindowInsetsListener(this.window.decorView) {
                v: View, insets: WindowInsetsCompat ->
            val b = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            val params = view.layoutParams
            params.height = rootViewBottom + b
            view.layoutParams = params
            ViewCompat.onApplyWindowInsets(v, insets)
            return@setOnApplyWindowInsetsListener insets
        }
    }

    protected open fun onViewSetup() { }

    @Suppress("PropertyName")
    protected val STATE: Bundle = Bundle()
    override fun onSaveInstanceState(outState: Bundle) {
        STATE.takeIf { !STATE.isEmpty }?.let {
            outState.putAll(STATE)
        }
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        STATE.clear()
        _binding = null
        unregister()
        super.onDestroy()
    }

    protected open fun isActivityAtBottom(): Boolean = false

    private var last: Long = -1
    private fun applyBackPressed(): Boolean {
        supportFragmentManager.fragments.forEach {
            if (it is BaseFragment<*> && it.onBackPressed()) {
                return true
            }
        }
        if (!isActivityAtBottom()){
            return false
        }
        val now = System.currentTimeMillis()
        if (last == -1L) {
            Application.onToast(this, R.string.text_back_exit)
            last = now
        } else {
            if (now - last < 2000) {
                finishAll()
            } else {
                last = now
                Application.onToast(this, R.string.text_back_exit_notice)
            }
        }
        return true
    }
}