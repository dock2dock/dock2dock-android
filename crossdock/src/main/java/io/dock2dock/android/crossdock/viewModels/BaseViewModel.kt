package io.dock2dock.android.crossdock.viewModels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

abstract class BaseViewModel: ViewModel() {
    internal val _successMessage: MutableStateFlow<String?> = MutableStateFlow(null)
    internal val successMessage: StateFlow<String?> get() = _successMessage

    internal val _isSnackBarShowing: MutableStateFlow<Boolean> = MutableStateFlow(false)
    internal val isSnackBarShowing: StateFlow<Boolean> get() = _isSnackBarShowing

    internal fun onDismissSnackBar() {
        _isSnackBarShowing.value = false
    }
}