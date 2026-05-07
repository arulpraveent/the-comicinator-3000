package com.deepvisiontech.thecomicinator3000.features.comic.presentation.viewmodels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.deepvisiontech.thecomicinator3000.R
import com.deepvisiontech.thecomicinator3000.core.domain.model.EvilResponse
import com.deepvisiontech.thecomicinator3000.core.domain.model.UiText
import com.deepvisiontech.thecomicinator3000.features.comic.domain.model.Comic
import com.deepvisiontech.thecomicinator3000.features.comic.domain.usecase.GetComicFlowUseCase
import com.deepvisiontech.thecomicinator3000.features.comic.domain.usecase.GetComicPagesUseCase
import com.deepvisiontech.thecomicinator3000.features.comic.presentation.navigation.ComicRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class ComicUiState(
    val isLoading: Boolean = true,
    val comicPages: List<Uri> = emptyList(),
    val comic: Comic = Comic(
        "",
        null,
        "Not Available",
        System.currentTimeMillis(),
        "",
        "",
        0,
        false,
        null
    )
) {
    val numberOfPages: Int
        get() = comicPages.size
}

sealed interface ComicUiEvent {
    data class Error(val message: UiText): ComicUiEvent
}

@HiltViewModel
class ComicViewModel @Inject constructor(
    private val getComicFlowUseCase: GetComicFlowUseCase,
    private val getComicPagesUseCase: GetComicPagesUseCase,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val _comicArgs = savedStateHandle.toRoute<ComicRoute.ComicScreen>()
    private val _comicId = _comicArgs.comicId
    private val _uiEvent = MutableSharedFlow<ComicUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<ComicUiState> = getComicFlowUseCase(_comicId)
        .mapLatest { comic ->
            val pagesResult = getComicPagesUseCase(comic)

            println("$comic ////////////")

            if (pagesResult is EvilResponse.Success) {
                ComicUiState(
                    isLoading = false,
                    comicPages = pagesResult.data,
                    comic = comic
                )
            } else {
                _uiEvent.emit(
                    ComicUiEvent.Error(UiText.StringResource(R.string.comic_error_load_failed))
                )
                ComicUiState(
                    isLoading = false,
                    comicPages = emptyList(),
                    comic = comic
                )
            }
        }
        .catch { exception ->
            _uiEvent.emit(
                ComicUiEvent.Error(UiText.StringResource(R.string.comic_error_load_failed))
            )
            Log.e("ComicViewModel", "Error loading comic stream:", exception)
            emit(ComicUiState(isLoading = false))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ComicUiState(isLoading = true)
        )
}