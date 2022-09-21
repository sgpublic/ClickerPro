package io.github.clickerpro.ui.overlay

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.MutableLiveData
import io.github.clickerpro.R
import io.github.clickerpro.base.BaseOverlayWidget
import io.github.clickerpro.core.util.dp
import io.github.clickerpro.core.util.dpFloat
import io.github.clickerpro.core.util.take
import io.github.clickerpro.databinding.OverlayBilibiliPriconneItemSwitcherBinding

class BiliConneCardItemSwitcherOverlay(
    context: Context, private val size: BiliConneCardOverlay.Size,
    private val flag: List<MutableLiveData<Boolean>>
): BaseOverlayWidget<OverlayBilibiliPriconneItemSwitcherBinding>(context) {
    private var onClick: OnClickListener? = null
    private val cardElevation = 6

    fun setOnClickListener(onClick: OnClickListener) {
        this.onClick = onClick
    }

    private val Cards: List<Pair<CardView, TextView>> by lazy {
        arrayListOf(
            Pair(ViewBinding.overlayBilibiliPriconneSwitcher1, ViewBinding.overlayBilibiliPriconneSwitcher1Title),
            Pair(ViewBinding.overlayBilibiliPriconneSwitcher2, ViewBinding.overlayBilibiliPriconneSwitcher2Title),
            Pair(ViewBinding.overlayBilibiliPriconneSwitcher3, ViewBinding.overlayBilibiliPriconneSwitcher3Title),
            Pair(ViewBinding.overlayBilibiliPriconneSwitcher4, ViewBinding.overlayBilibiliPriconneSwitcher4Title),
            Pair(ViewBinding.overlayBilibiliPriconneSwitcher5, ViewBinding.overlayBilibiliPriconneSwitcher5Title),
        )
    }

    override fun onCreate() {
        flag.forEachIndexed { index, life ->
            life.observe(this) { checked ->
                Cards[index].first.setCardBackgroundColor(context.getColor(
                    checked.take(R.color.colorPrimary, R.color.colorOnPrimary)
                ))
                Cards[index].second.run {
                    setTextColor(context.getColor(
                        checked.take(R.color.colorOnPrimary, R.color.colorPrimaryText)
                    ))
                    setOnTouchListener { v, event ->
                        if (event.action == MotionEvent.ACTION_DOWN) {
                            life.postValue(!checked)
                        }
                        if (event.action == MotionEvent.ACTION_UP) {
                            v.performClick()
                        }
                        return@setOnTouchListener false
                    }
                    text = context.getText(checked.take(
                        R.string.overlay_item_switcher_on, R.string.overlay_item_switcher_off
                    ))
                }
            }
        }
    }
    
    override fun onSetupView() {
        super.onSetupView()
        Cards.forEachIndexed { index, (card, _) ->
            card.layoutParams = (card.layoutParams as ConstraintLayout.LayoutParams).also {
                it.width = size.cardSize()
                when(index) {
                    0, 1 -> it.marginEnd = size.divider()
                    3, 4 -> it.marginStart = size.divider()
                }
            }
            card.cardElevation = cardElevation.dpFloat
            card.radius = 6.dpFloat
        }
    }

    override fun onSetupLayoutParams(lp: WindowManager.LayoutParams) {
        lp.width = size.width() + (cardElevation * 2).dp
        lp.height = (30 + cardElevation * 2).dp
        lp.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        lp.y = size.seat() + size.height() + 10.dp
        lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
    }

    interface OnClickListener {
        fun onClick(index: Int)
    }

    override val ViewBinding: OverlayBilibiliPriconneItemSwitcherBinding =
        OverlayBilibiliPriconneItemSwitcherBinding.inflate(LayoutInflater.from(context))
}