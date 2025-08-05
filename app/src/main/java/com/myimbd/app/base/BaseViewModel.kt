package com.myimbd.app.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class BaseViewModel : ViewModel() {

    // Generic loading state for all VMs
    protected val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    // Generic error state for all VMs
    protected val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    // Helper function to set error
    fun setError(message: String?) {
        _error.value = message
    }

    // Helper function to set loading
    protected fun setLoading(loading: Boolean) {
        _isLoading.value = loading
    }
}
