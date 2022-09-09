package io.github.clickerpro.fragment.mode

import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import io.github.clickerpro.R
import io.github.clickerpro.base.BaseFragment
import io.github.clickerpro.core.manager.ConfigManager
import io.github.clickerpro.core.util.Clicker
import io.github.clickerpro.databinding.FragmentModeSettingDetailBinding


class ModeSettingRootFragment(contest: AppCompatActivity)
    : BaseFragment<FragmentModeSettingDetailBinding>(contest) {
    override fun onFragmentCreated(hasSavedInstanceState: Boolean) {

    }

    override fun onViewSetup() {
        ViewBinding.fragmentModeDetailTitle.text = getText(R.string.mode_setting_activity_root_permission_title)
        ViewBinding.fragmentModeDetailDesc.text = getText(R.string.mode_setting_activity_root_permission_desc)
        ViewBinding.fragmentModeDetailSwitch.run {
            setOnClickListener {
                ConfigManager.ROOT = Clicker.Root.request().also { request ->
                    if (request) return@also
                    Snackbar.make(
                        ViewBinding.root,
                        R.string.mode_setting_activity_root_denied,
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
            setOnCheckedChangeListener { buttonView, isChecked ->
                buttonView.isEnabled = !isChecked
            }
            Clicker.Root.AVAILABLE.observe(this@ModeSettingRootFragment) {
                isChecked = it
            }
        }
        if (!Clicker.Root.check()) {
            ViewBinding.fragmentModeDetailSwitch.isEnabled = false
            ViewBinding.fragmentModeDetailSwitch.isChecked = false
        } else {
            Clicker.Root.AVAILABLE.observe(this) {
                ViewBinding.fragmentModeDetailSwitch.isChecked = it
            }
        }
    }

    override fun onCreateViewBinding(container: ViewGroup?) =
        FragmentModeSettingDetailBinding.inflate(layoutInflater, container, false)
}