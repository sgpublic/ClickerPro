package io.github.clickerpro.activity

import android.content.Context
import android.content.Intent
import android.view.MenuItem
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import io.github.clickerpro.R
import io.github.clickerpro.base.BaseActivity
import io.github.clickerpro.core.manager.ConfigManager
import io.github.clickerpro.core.util.take
import io.github.clickerpro.databinding.ActivityModeSettingBinding
import io.github.clickerpro.ui.adapter.ModeSettingPagerAdapter

class ModeSettingActivity: BaseActivity<ActivityModeSettingBinding>() {
    override fun onActivityCreated(hasSavedInstanceState: Boolean) {

    }

    override fun onViewSetup() {
        setSupportActionBar(ViewBinding.modeSettingToolbar)
        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            setTitle(R.string.mode_setting_activity_title)
        }
        ViewBinding.modeSettingOverlay.setOnClickListener {
            if (!ViewBinding.modeSettingOverlaySwitch.isChecked) {
                ViewBinding.modeSettingOverlaySwitch.isChecked = true
            }
        }
        ViewBinding.modeSettingOverlaySwitch.run {
            setOnCheckedChangeListener { buttonView, isChecked ->
                buttonView.isEnabled = !isChecked
                val visibility = isChecked.take(View.VISIBLE, View.GONE)
                ViewBinding.modeSettingModeToggle.visibility = visibility
                ViewBinding.modeSettingModeDetail.visibility = visibility
            }
            setOnClickListener {
                XXPermissions.with(this@ModeSettingActivity)
                    .permission(Permission.SYSTEM_ALERT_WINDOW)
                    .request(object : OnPermissionCallback {
                        override fun onGranted(permissions: MutableList<String>?, all: Boolean) {

                        }

                        override fun onDenied(permissions: MutableList<String>?, never: Boolean) {
                            isChecked = false
                            Snackbar.make(
                                ViewBinding.root,
                                R.string.mode_setting_activity_overlay_desc,
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                    })
            }
        }
        ViewBinding.modeSettingModeDetail.run {
            isUserInputEnabled = false
            adapter = ModeSettingPagerAdapter(this@ModeSettingActivity)
        }
        ViewBinding.modeSettingModeToggle.run {
            addOnButtonCheckedListener { _, checkedId, isChecked ->
                if (!isChecked) return@addOnButtonCheckedListener
                when(checkedId) {
                    R.id.mode_setting_mode_accessibility -> {
                        ConfigManager.MODE = ConfigManager.Mode.ACCESSIBILITY
                        ViewBinding.modeSettingModeDetail.currentItem = 0
                    }
                    R.id.mode_setting_mode_root -> {
                        ConfigManager.MODE = ConfigManager.Mode.ROOT
                        ViewBinding.modeSettingModeDetail.currentItem = 1
                    }
                }
            }
            when(ConfigManager.MODE) {
                ConfigManager.Mode.ACCESSIBILITY -> check(R.id.mode_setting_mode_accessibility)
                ConfigManager.Mode.ROOT -> check(R.id.mode_setting_mode_root)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
        }
        return true
    }

    override fun onResume() {
        super.onResume()
        ViewBinding.modeSettingOverlaySwitch.isChecked =
            XXPermissions.isGranted(this@ModeSettingActivity, Permission.SYSTEM_ALERT_WINDOW)
    }

    override fun onCreateViewBinding(): ActivityModeSettingBinding =
        ActivityModeSettingBinding.inflate(layoutInflater)

    companion object {
        fun startActivity(origin: Context) {
            origin.startActivity(Intent(origin, ModeSettingActivity::class.java))
        }
    }
}