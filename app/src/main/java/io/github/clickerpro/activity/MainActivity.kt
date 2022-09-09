package io.github.clickerpro.activity

import android.content.res.ColorStateList
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import io.github.clickerpro.R
import io.github.clickerpro.base.BaseVMActivity
import io.github.clickerpro.core.manager.ConfigManager
import io.github.clickerpro.core.manager.RuntimeValues
import io.github.clickerpro.core.util.Clicker
import io.github.clickerpro.core.util.dp
import io.github.clickerpro.core.util.take
import io.github.clickerpro.core.util.toggle
import io.github.clickerpro.databinding.ActivityMainBinding
import io.github.clickerpro.databinding.ItemHomePrimaryActionBinding
import io.github.clickerpro.service.ClickerAccessibilityService
import io.github.clickerpro.viewmodel.MainViewModel

class MainActivity : BaseVMActivity<ActivityMainBinding, MainViewModel>() {
    override fun onActivityCreated(hasSavedInstanceState: Boolean) {

    }

    override fun onViewSetup() {
        ViewBinding.homeActionSwitcher.run switcher@{
            itemHomePrimaryActionBase.run {
                isCheckable = true
                setOnClickListener {
                    ViewModel.SWITCHER.toggle()
                }
                onSwitcherClose(this@switcher)
            }
            itemHomePrimaryActionTitle.setTextColor(getColor(R.color.colorOnPrimary))
            itemHomePrimaryActionDesc.setTextColor(getColor(R.color.colorOnPrimary))
            itemHomePrimaryActionIcon.imageTintList = ColorStateList.valueOf(getColor(R.color.colorOnPrimary))
        }
        ViewBinding.homeActionMode.run {
            itemHomePrimaryActionBase.run {
                strokeWidth = 1.dp
                setOnClickListener {
                    ModeSettingActivity.startActivity(this@MainActivity)
                }
            }
            itemHomePrimaryActionIcon.run {
                setImageResource(R.drawable.ic_mode)
                imageTintList = ColorStateList.valueOf(getColor(R.color.colorPrimaryText))
            }
            itemHomePrimaryActionTitle.text = getText(R.string.main_activity_mode)
        }
    }

    override fun onViewModelSetup() {
        ViewModel.AVAILABLE.observe(this) {
            ViewBinding.homeActionSwitcher.run {
                itemHomePrimaryActionBase.isEnabled = it
                if (!it) {
                    itemHomePrimaryActionBase.isChecked = false
                    itemHomePrimaryActionDesc.text = getString(R.string.main_activity_service_unusable_desc)
                } else {
                    itemHomePrimaryActionDesc.text = getString(itemHomePrimaryActionBase.isChecked.take(
                        R.string.main_activity_service_started_desc, R.string.main_activity_service_stop_desc
                    ))
                }
            }
        }
        ViewModel.SWITCHER.observe(this) { isChecked ->
            RuntimeValues.OVERLAY_OPEN = isChecked
            ViewBinding.homeActionMode.itemHomePrimaryActionBase.run {
                isEnabled = !isChecked
                alpha = isChecked.take(0.5f, 1f)
            }
            ViewBinding.homeActionSwitcher.run {
                itemHomePrimaryActionBase.isChecked = isChecked
                if (isChecked) {
                    onSwitcherOpen(this)
                } else {
                    onSwitcherClose(this)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Clicker.Root.AVAILABLE.removeObservers(this)
        ClickerAccessibilityService.STARTED.removeObservers(this)
        when (ConfigManager.MODE) {
            ConfigManager.Mode.ROOT -> {
                Clicker.Root.AVAILABLE.observe(this) { AVAILABLE ->
                    ViewModel.AVAILABLE.postValue(AVAILABLE)
                    ViewBinding.homeActionMode.itemHomePrimaryActionDesc.text = getString(
                        R.string.main_activity_usage,
                        getString(R.string.mode_setting_activity_root_mode),
                        getString(AVAILABLE.take(
                            R.string.main_activity_usable, R.string.main_activity_unusable
                        ))
                    )
                }
            }
            ConfigManager.Mode.ACCESSIBILITY -> {
                ClickerAccessibilityService.STARTED.observe(this) { STARTED ->
                    ViewModel.AVAILABLE.postValue(STARTED)
                    ViewBinding.homeActionMode.itemHomePrimaryActionDesc.text = getString(
                        R.string.main_activity_usage,
                        getString(R.string.mode_setting_activity_accessibility_mode),
                        getString(STARTED.take(
                            R.string.main_activity_usable, R.string.main_activity_unusable
                        ))
                    )
                }
            }
        }
    }

    private fun onSwitcherOpen(binding: ItemHomePrimaryActionBinding) {
        binding.run {
            itemHomePrimaryActionBase.setCardBackgroundColor(getColor(R.color.home_switcher_checked))
            itemHomePrimaryActionIcon.setImageResource(R.drawable.ic_check)
            itemHomePrimaryActionTitle.text = getString(R.string.main_activity_service_started)
            itemHomePrimaryActionDesc.text = getString(R.string.main_activity_service_started_desc)
        }
        ClickerAccessibilityService.createOverlay()
    }

    private fun onSwitcherClose(binding: ItemHomePrimaryActionBinding) {
        binding.run {
            itemHomePrimaryActionBase.setCardBackgroundColor(getColor(R.color.home_switcher_common))
            itemHomePrimaryActionIcon.setImageResource(R.drawable.ic_block)
            itemHomePrimaryActionTitle.text = getString(R.string.main_activity_service_stop)
            itemHomePrimaryActionDesc.text = getString(R.string.main_activity_service_stop_desc)
        }
        ClickerAccessibilityService.destroyOverlay()
    }

    override fun beforeCreate() {
        super.beforeCreate()
        installSplashScreen()
    }

    override fun onCreateViewBinding(): ActivityMainBinding =
        ActivityMainBinding.inflate(layoutInflater)

    override val ViewModel: MainViewModel by viewModels()

    override fun isActivityAtBottom(): Boolean = true

    override fun onDestroy() {
        super.onDestroy()
        ClickerAccessibilityService.destroyOverlay()
    }
}