package io.github.clickerpro.ui.overlay

import android.content.Context
import android.content.res.ColorStateList
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import io.github.clickerpro.Application
import io.github.clickerpro.R
import io.github.clickerpro.base.BaseVMOverlayWidget
import io.github.clickerpro.core.util.*
import io.github.clickerpro.databinding.OverlayBlilbiliPriconneCardBinding
import io.github.clickerpro.viewmodel.BiliConneCardViewModel
import kotlin.math.roundToInt

class BiliConneCardOverlay(context: Context):
    BaseVMOverlayWidget<OverlayBlilbiliPriconneCardBinding, BiliConneCardViewModel>(context),
    Clicker.OnTouchListener {
    override val ViewBinding: OverlayBlilbiliPriconneCardBinding =
        OverlayBlilbiliPriconneCardBinding.inflate(LayoutInflater.from(context))
    override val ViewModel: BiliConneCardViewModel = BiliConneCardViewModel()

    private val Cards: List<ImageView> by lazy {
        arrayListOf(
            ViewBinding.overlayBilibiliPriconneCard1,
            ViewBinding.overlayBilibiliPriconneCard2,
            ViewBinding.overlayBilibiliPriconneCard3,
            ViewBinding.overlayBilibiliPriconneCard4,
            ViewBinding.overlayBilibiliPriconneCard5,
        )
    }

    private val xRanges: ArrayList<IntRange> = { 0 until 1 }.repeatToList(5)

    override fun onCreate() {
        CardCover = BiliConneCardItemSwitcherOverlay(context, size, ViewModel.CARD_CLICK).also {
            it.create()
            it.setOnClickListener(object : BiliConneCardItemSwitcherOverlay.OnClickListener {
                override fun onClick(index: Int) {
                    ViewModel.CARD_CLICK[index].toggle()
                }
            })
        }
    }

    override fun onViewModelSetup() {
        ViewModel.CARD_CLICK.forEachIndexed { index, live ->
            live.observe(this) { checked ->
                Cards[index].imageTintList = ColorStateList.valueOf(context.getColor(
                    checked.take(R.color.overlay_bilibili_priconne_card, R.color.home_switcher_common)
                ))
                if (checked) {
                    val card = xRanges[index]
                    val point = Clicker.Point(
                        (card.first + card.last) / 2,
                        (yRange.first + yRange.last) / 2
                    )
                    log.debug("开始点击：$point")
                    Clicker.startClick(point, index)
                } else {
                    Clicker.stopClick(index)
                }
            }
        }
    }

    private var CardCover: BiliConneCardItemSwitcherOverlay? = null
    override fun onShow() {
        (size.screenHeight - size.seat()).let {
            yRange = (it - size.cardSize()) .. it
        }
        ((size.screenWidth - size.width()) / 2.0).let {
            Cards.forEachIndexed { index, imageView ->
                xRanges[index] = (it + imageView.x).roundToInt() .. (it + imageView.x + size.cardSize()).roundToInt()
            }
        }
        CardCover?.show()
    }

    private val size by lazy {
        Size(Application.DISPLAY_METRICS)
    }
    override fun onSetupLayoutParams(lp: WindowManager.LayoutParams) {
        lp.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        lp.width = size.width()
        lp.height = size.height()
        lp.y = size.seat()
        lp.flags = lp.flags or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
    }

    override fun onSetupView() {
        super.onSetupView()
        Cards.forEachIndexed { index, card ->
            card.layoutParams = (card.layoutParams as ConstraintLayout.LayoutParams).also {
                it.width = size.cardSize()
                it.height = size.cardSize()
                when(index) {
                    0, 1 -> it.marginEnd = size.divider()
                    3, 4 -> it.marginStart = size.divider()
                }
            }
        }
    }

    private var yRange: IntRange = 0 until 1
    override fun onTouch(point: Clicker.Point) {
        if (point.y in yRange) {
            return
        }
        xRanges.forEachIndexed { index, xRange ->
            if (point.x in xRange) {
                ViewModel.CARD_CLICK[index].toggle()
                return
            }
        }
    }

    override fun onHide() {
        for (data in ViewModel.CARD_CLICK) {
            data.postValue(false)
        }
        Clicker.clear()
        CardCover?.hide()
    }

    override fun onDestroy() {
        CardCover?.destroy()
        CardCover = null
        onHide()
    }

    /**
     * 本地测试：
     * - 2340x1080
     * - 6.39 寸
     * - DPI：392
     * - display.density：2.75
     */
    class Size(display: DisplayMetrics) {
        val screenWidth = display.widthPixels.coerceAtLeast(display.heightPixels)
        val screenHeight = display.widthPixels.coerceAtMost(display.heightPixels)

        private val cardSize = screenHeight * 0.175 + 8.dpFloat
        private val divider = cardSize * 0.17
        private val seat = cardSize * 0.49

        fun cardSize(): Int = cardSize.roundToInt()
        fun divider(): Int = divider.roundToInt()
        fun seat(): Int = seat.roundToInt()

        fun height(): Int = cardSize.roundToInt()
        fun width(): Int = (cardSize * 5 + divider * 4).roundToInt()
    }
}