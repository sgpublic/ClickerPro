package io.github.clickerpro.base

import android.content.Context
import androidx.annotation.CallSuper
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding

abstract class BaseVMOverlayWidget<VB: ViewBinding, VM: ViewModel>(context: Context):
    BaseOverlayWidget<VB>(context) {
    @Suppress("PropertyName")
    protected abstract val ViewModel: VM

    @CallSuper
    override fun beforeCreate() {
        onViewModelSetup()
    }

    protected open fun onViewModelSetup() { }
}