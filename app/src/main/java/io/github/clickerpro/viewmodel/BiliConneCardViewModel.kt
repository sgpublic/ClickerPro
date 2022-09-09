package io.github.clickerpro.viewmodel

import androidx.lifecycle.MutableLiveData
import io.github.clickerpro.base.BaseViewModel
import io.github.clickerpro.core.util.repeatToList

class BiliConneCardViewModel: BaseViewModel() {
    val CARD_CLICK: List<MutableLiveData<Boolean>> = { MutableLiveData(false) }.repeatToList(5)
}
