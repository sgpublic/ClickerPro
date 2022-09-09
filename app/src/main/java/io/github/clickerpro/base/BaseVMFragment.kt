package io.github.clickerpro.base

import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding

abstract class BaseVMFragment<VB: ViewBinding, VM: ViewModel>(context: AppCompatActivity)
    : BaseFragment<VB>(context) {
    @Suppress("PropertyName")
    protected abstract val ViewModel: VM

    @CallSuper
    override fun beforeFragmentCreated() {
        onViewModelSetup()
    }

    protected open fun onViewModelSetup() { }
}