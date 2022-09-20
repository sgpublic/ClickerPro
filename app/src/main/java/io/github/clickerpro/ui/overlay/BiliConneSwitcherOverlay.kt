package io.github.clickerpro.ui.overlay

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import io.github.clickerpro.Application
import io.github.clickerpro.R
import io.github.clickerpro.base.BaseVMOverlayWidget
import io.github.clickerpro.core.manager.ConfigManager
import io.github.clickerpro.core.util.take
import io.github.clickerpro.databinding.OverlayBilibiliPriconneBinding
import io.github.clickerpro.service.ClickerAccessibilityService
import io.github.clickerpro.viewmodel.BiliConneSwitcherViewModel
import kotlin.math.roundToInt

class BiliConneSwitcherOverlay(context: Context):
    BaseVMOverlayWidget<OverlayBilibiliPriconneBinding, BiliConneSwitcherViewModel>(context) {
    override val ViewBinding: OverlayBilibiliPriconneBinding =
        OverlayBilibiliPriconneBinding.inflate(LayoutInflater.from(context))
    override val ViewModel: BiliConneSwitcherViewModel = BiliConneSwitcherViewModel()

    private var Card: BiliConneCardOverlay? = null

    override fun onSetupView() {
        super.onSetupView()
        ViewBinding.overlayState.text = Application.getString(R.string.overlay_prc_title)
    }

    override fun onViewModelSetup() {
        ViewModel.CHECKED.observe(this) { checked ->
            ViewBinding.overlaySwitcher.setOnClickListener {
                ViewModel.CHECKED.postValue(!checked)
            }
            ViewBinding.overlaySwitcher.setCardBackgroundColor(context.getColor(
                checked.take(R.color.colorPrimary, R.color.colorOnPrimary)
            ))
            ViewBinding.overlayState.setTextColor(context.getColor(
                checked.take(R.color.colorOnPrimary, R.color.colorPrimaryText)
            ))
            if (checked) {
                Card?.show()
            } else {
                Card?.hide()
            }
        }
    }

    override fun onOverlayDrag(x: Float, y: Float) {
        ConfigManager.OVERLAY_X = x.roundToInt()
        ConfigManager.OVERLAY_Y = y.roundToInt()
    }

    override fun onSetupLayoutParams(lp: WindowManager.LayoutParams) {
        lp.flags = lp.flags or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        lp.x = ConfigManager.OVERLAY_X
        lp.y = ConfigManager.OVERLAY_Y
    }

    override fun onCreate() {
        Card = create(ClickerAccessibilityService.getInstance() ?: return)
        Card?.create()
    }

    override fun onHide() {
        Card?.hide()
    }

    override fun onDestroy() {
        super.onDestroy()
        Card?.destroy()
    }

    override fun recreate() {
        Card?.recreate()
        super.recreate()
    }

    override val DRAG_VIEW: View = ViewBinding.overlaySwitcher
}