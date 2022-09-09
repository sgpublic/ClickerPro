package io.github.clickerpro.viewmodel

import androidx.lifecycle.MutableLiveData
import io.github.clickerpro.base.BaseViewModel

class BiliConneSwitcherViewModel: BaseViewModel() {
    val CHECKED: MutableLiveData<Boolean> = MutableLiveData(false)
}