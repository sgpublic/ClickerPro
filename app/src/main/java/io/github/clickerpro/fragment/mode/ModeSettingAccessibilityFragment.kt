package io.github.clickerpro.fragment.mode

import android.content.Intent
import android.provider.Settings
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import io.github.clickerpro.R
import io.github.clickerpro.base.BaseFragment
import io.github.clickerpro.databinding.FragmentModeSettingDetailBinding
import io.github.clickerpro.service.ClickerAccessibilityService


class ModeSettingAccessibilityFragment(contest: AppCompatActivity)
    : BaseFragment<FragmentModeSettingDetailBinding>(contest) {
    override fun onFragmentCreated(hasSavedInstanceState: Boolean) {

    }

    override fun onViewSetup() {
        ViewBinding.fragmentModeDetailTitle.text = getText(R.string.mode_setting_activity_accessibility_permission_title)
        ViewBinding.fragmentModeDetailDesc.text = getText(R.string.mode_setting_activity_accessibility_permission_desc)
        ViewBinding.fragmentModeDetailSwitch.run {
            setOnClickListener {
                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
                isChecked = false
            }
            setOnCheckedChangeListener { _, isChecked ->
                isEnabled = !isChecked
            }
            ClickerAccessibilityService.STARTED.observe(this@ModeSettingAccessibilityFragment) {
                isChecked = it
            }
        }
    }

    override fun onCreateViewBinding(container: ViewGroup?) =
        FragmentModeSettingDetailBinding.inflate(layoutInflater, container, false)
}