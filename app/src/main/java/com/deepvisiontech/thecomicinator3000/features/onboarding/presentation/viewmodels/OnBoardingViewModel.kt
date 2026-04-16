package com.deepvisiontech.thecomicinator3000.features.onboarding.presentation.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deepvisiontech.thecomicinator3000.features.common.domain.model.EvilResponse
import com.deepvisiontech.thecomicinator3000.features.onboarding.domain.usecase.GetStoragePermissionStateFlowUseCase
import com.deepvisiontech.thecomicinator3000.features.onboarding.domain.usecase.GetStoragePermissionUriFlow
import com.deepvisiontech.thecomicinator3000.features.onboarding.domain.usecase.SetStoragePermissionStateUseCase
import com.deepvisiontech.thecomicinator3000.features.onboarding.domain.usecase.SetStorageUriUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OnBoardingState(
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
        val uri: Uri
    ): OnBoardingScreenAction

    data object OnPermissionCheckPassed: OnBoardingScreenAction

    data object OnAccessDenied: OnBoardingScreenAction
}

@HiltViewModel
class OnBoardingViewModel @Inject constructor(
    private val setStorageUriUseCase: SetStorageUriUseCase,
    private val setStoragePermissionStateUseCase: SetStoragePermissionStateUseCase,
    getStoragePermissionStateFlowUseCase: GetStoragePermissionStateFlowUseCase,
    getStoragePermissionUriFlow: GetStoragePermissionUriFlow
): ViewModel() {

    val uiState: StateFlow<OnBoardingState> = combine(
        getStoragePermissionStateFlowUseCase(),
        getStoragePermissionUriFlow()
    ) { isGranted, uri ->
        OnBoardingState(
            isPermissionGranted = isGranted,
            storageUri = uri?.toString() ?: ""
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = OnBoardingState()
    )

    private val _uiEvent = MutableSharedFlow<OnBoardingScreenEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private fun onAccessGranted(uri: Uri) {
        viewModelScope.launch {
            val uriResponse = setStorageUriUseCase(uri)
            if (uriResponse is EvilResponse.Failure) {
                _uiEvent.emit(OnBoardingScreenEvent.Error("Failed to save Storage URI"))
                return@launch
            }

            val stateResponse = setStoragePermissionStateUseCase(true)
            if (stateResponse is EvilResponse.Failure) {
                _uiEvent.emit(OnBoardingScreenEvent.Error("Failed to save Permission State"))
                return@launch
            }

            _uiEvent.emit(OnBoardingScreenEvent.NavigateToLibrary)
        }
    }

    private fun onAccessDenied() {
        viewModelScope.launch {
            val response = setStoragePermissionStateUseCase(false)

            if (response is EvilResponse.Failure) {
                _uiEvent.emit(OnBoardingScreenEvent.Error("Failed to update Permission State"))
                return@launch
            }
        }
    }

    private fun onPermissionCheckPassed() {
        viewModelScope.launch {
            val response = setStoragePermissionStateUseCase(true)

            if (response is EvilResponse.Failure) {
                _uiEvent.emit(OnBoardingScreenEvent.Error("Failed to update Permission State"))
                return@launch
            }

            _uiEvent.emit(OnBoardingScreenEvent.NavigateToLibrary)
        }
    }


    fun onAction(action: OnBoardingScreenAction) {
        when(action) {
            OnBoardingScreenAction.OnAccessDenied -> onAccessDenied()
            is OnBoardingScreenAction.OnAccessGranted -> onAccessGranted(action.uri)
            OnBoardingScreenAction.OnPermissionCheckPassed -> onPermissionCheckPassed()
        }
    }
}