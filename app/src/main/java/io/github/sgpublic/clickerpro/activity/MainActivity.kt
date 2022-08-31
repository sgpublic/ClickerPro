package io.github.sgpublic.clickerpro.activity

import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import io.github.sgpublic.clickerpro.base.BaseViewModelActivity
import io.github.sgpublic.clickerpro.databinding.ActivityMainBinding
import io.github.sgpublic.clickerpro.viewmodel.MainViewModel

class MainActivity : BaseViewModelActivity<ActivityMainBinding, MainViewModel>() {
    override fun onActivityCreated(hasSavedInstanceState: Boolean) {

    }

    override fun beforeCreate() {
        installSplashScreen()
    }

    override fun onCreateViewBinding(): ActivityMainBinding =
        ActivityMainBinding.inflate(layoutInflater)

    override val ViewModel: MainViewModel by viewModels()
}