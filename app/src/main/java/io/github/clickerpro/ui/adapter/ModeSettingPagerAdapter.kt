package io.github.clickerpro.ui.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import io.github.clickerpro.fragment.mode.ModeSettingAccessibilityFragment
import io.github.clickerpro.fragment.mode.ModeSettingRootFragment

class ModeSettingPagerAdapter(private val activity: AppCompatActivity)
    : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ModeSettingAccessibilityFragment(activity)
            1 -> ModeSettingRootFragment(activity)
            else -> throw IllegalStateException("Fragment for index $position not found")
        }
    }
}