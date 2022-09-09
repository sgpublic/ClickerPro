package io.github.clickerpro.base

import androidx.annotation.CallSuper
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding

abstract class BaseVMActivity<VB: ViewBinding, VM: ViewModel>: BaseActivity<VB>() {
    @Suppress("PropertyName")
    protected abstract val ViewModel: VM

    @CallSuper
    override fun beforeCreate() {
        onViewModelSetup()
    }

    protected open fun onViewModelSetup() { }
}