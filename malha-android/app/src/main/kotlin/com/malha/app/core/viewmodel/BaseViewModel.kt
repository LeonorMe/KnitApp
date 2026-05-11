package com.malha.app.core.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * Base ViewModel that offers a Channel for one‑off UI events (snackbars, navigation, etc.).
 */
abstract class BaseViewModel : ViewModel() {
    private val _uiEvent = Channel<UiEvent>(Channel.BUFFERED)
    val uiEventFlow = _uiEvent.receiveAsFlow()

    protected fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch { _uiEvent.send(event) }
    }
}
