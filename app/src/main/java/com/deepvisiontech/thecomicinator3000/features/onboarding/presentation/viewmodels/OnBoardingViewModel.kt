package com.deepvisiontech.thecomicinator3000.features.onboarding.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deepvisiontech.thecomicinator3000.core.domain.model.EvilResponse
import com.deepvisiontech.thecomicinator3000.features.onboarding.domain.usecase.GetStoragePermissionUriFlow
import com.deepvisiontech.thecomicinator3000.features.onboarding.domain.usecase.SetStorageUriUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OnBoardingState(
    val isLoading: Boolean = true,
    val isPermissionGranted: Boolean = false,
    val storageUri: String = ""
)

sealed interface OnBoardingScreenEvent {
    data class Error(
        val message: String
    ): OnBoardingScreenEvent

    data object NavigateToLibrary: OnBoardingScreenEvent
}

sealed interface OnBoardingScreenAction {
    data class OnAccessGranted(
        val uri: String
    ): OnBoardingScreenAction

    data object OnAccessDenied: OnBoardingScreenAction
}

@HiltViewModel
class OnBoardingViewModel @Inject constructor(
    private val setStorageUriUseCase: SetStorageUriUseCase,
    private val getStoragePermissionUriFlow: GetStoragePermissionUriFlow
): ViewModel() {

    val uiState: StateFlow<OnBoardingState> =
        getStoragePermissionUriFlow().map{ uri ->
        OnBoardingState(
            isLoading = false,
            isPermissionGranted = uri != null,
            storageUri = uri ?: ""
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = OnBoardingState(isLoading = true)
    )

    private val _uiEvent = MutableSharedFlow<OnBoardingScreenEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        performInitialCheck()
    }

    private fun performInitialCheck() {
        viewModelScope.launch {
            val uri = getStoragePermissionUriFlow().first()

            if (!uri.isNullOrBlank()) {
                _uiEvent.emit(OnBoardingScreenEvent.NavigateToLibrary)
            }
        }
    }

    private fun onAccessGranted(uri: String) {
        viewModelScope.launch {
            val uriResponse = setStorageUriUseCase(uri)
            if (uriResponse is EvilResponse.Failure) {
                _uiEvent.emit(OnBoardingScreenEvent.Error("Failed to save Storage URI"))
                return@launch
            }

            _uiEvent.emit(OnBoardingScreenEvent.NavigateToLibrary)
        }
    }

    private fun onAccessDenied() {
        viewModelScope.launch {
            val response = setStorageUriUseCase(null)

            if (response is EvilResponse.Failure) {
                _uiEvent.emit(OnBoardingScreenEvent.Error("Failed to update Permission State"))
                return@launch
            }
        }
    }


    fun onAction(action: OnBoardingScreenAction) {
        when(action) {
            OnBoardingScreenAction.OnAccessDenied -> onAccessDenied()
            is OnBoardingScreenAction.OnAccessGranted -> onAccessGranted(action.uri)
        }
    }
}