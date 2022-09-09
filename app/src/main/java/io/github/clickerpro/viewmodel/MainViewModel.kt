package io.github.clickerpro.viewmodel

import androidx.lifecycle.MutableLiveData
import io.github.clickerpro.base.BaseViewModel

class MainViewModel: BaseViewModel() {
    val AVAILABLE: MutableLiveData<Boolean> = MutableLiveData(false)
    val SWITCHER: MutableLiveData<Boolean> = MutableLiveData(false)
}
