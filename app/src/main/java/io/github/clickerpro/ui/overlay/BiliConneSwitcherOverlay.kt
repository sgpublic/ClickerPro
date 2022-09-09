package io.github.clickerpro.ui.overlay

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import io.github.clickerpro.Application
import io.github.clickerpro.R
import io.github.clickerpro.base.BaseVMOverlayWidget
import io.github.clickerpro.core.util.take
import io.github.clickerpro.databinding.OverlayBilibiliPriconneBinding
import io.github.clickerpro.viewmodel.BiliConneSwitcherViewModel

class BiliConneSwitcherOverlay(context: Context):
    BaseVMOverlayWidget<OverlayBilibiliPriconneBinding, BiliConneSwitcherViewModel>(context) {
    override val ViewBinding: OverlayBilibiliPriconneBinding =
        OverlayBilibiliPriconneBinding.inflate(LayoutInflater.from(context))
    override val ViewModel: BiliConneSwitcherViewModel = BiliConneSwitcherViewModel()

    private var Card: BiliConneCardOverlay = create(context)

    @SuppressLint("ClickableViewAccessibility")
    override fun onSetupView() {
        super.onSetupView()
        ViewBinding.overlayState.text = Application.getString(R.string.overlay_prc_title)
        ViewModel.CHECKED.value.take({ Card.show() }, { Card.hide() })
    }

    override fun onViewModelSetup() {
        ViewModel.CHECKED.observe(this) { checked ->
            ViewBinding.overlaySwitcher.setCardBackgroundColor(context.getColor(
                checked.take(R.color.colorPrimary, R.color.colorOnPrimary)
            ))
            ViewBinding.overlayState.setTextColor(context.getColor(
                checked.take(R.color.colorOnPrimary, R.color.colorPrimaryText)
            ))
            ViewBinding.overlaySwitcher.setOnClickListener {
                ViewModel.CHECKED.postValue(!checked)
            }
            if (checked) {
                Card.show()
            } else {
                Card.hide()
            }
        }
//        ViewBinding.root.setOnTouchListener { _, event ->
//            if (event.action == MotionEvent.ACTION_OUTSIDE) {
//                log.debug("click outside")
//            }
//            return@setOnTouchListener false
//        }
    }

//    override fun onSetupLayoutParams(lp: WindowManager.LayoutParams) {
//        lp.flags = lp.flags or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
//    }

    override fun onCreate() {
        Card.create()
    }

    override fun onHide() {
        Card.hide()
    }

    override fun onDestroy() {
        super.onDestroy()
        onHide()
        Card.destroy()
    }

    override fun recreate() {
        Card.recreate()
        super.recreate()
    }

    override val DRAG_VIEW: View = ViewBinding.overlaySwitcher
}