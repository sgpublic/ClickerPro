package io.github.clickerpro.activity

import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import io.github.clickerpro.base.BaseViewModelActivity
import io.github.clickerpro.databinding.ActivityMainBinding
import io.github.clickerpro.viewmodel.MainViewModel

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