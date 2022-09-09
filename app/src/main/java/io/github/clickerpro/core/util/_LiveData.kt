package io.github.clickerpro.core.util

import androidx.lifecycle.MutableLiveData

fun MutableLiveData<Boolean>.toggle() {
    postValue(value != true)
}