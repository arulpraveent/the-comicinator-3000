package com.deepvisiontech.thecomicinator3000.features.comic.presentation.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.deepvisiontech.thecomicinator3000.R
import com.deepvisiontech.thecomicinator3000.core.domain.model.EvilResponse
import com.deepvisiontech.thecomicinator3000.core.domain.model.UiText
import com.deepvisiontech.thecomicinator3000.features.comic.domain.model.Comic
import com.deepvisiontech.thecomicinator3000.features.comic.domain.model.ComicCollection
import com.deepvisiontech.thecomicinator3000.features.comic.domain.usecase.GetAllComicCollectionsUseCase
import com.deepvisiontech.thecomicinator3000.features.comic.domain.usecase.GetFilteredComicCollectionsUseCase
import com.deepvisiontech.thecomicinator3000.features.comic.domain.usecase.GetFilteredComicsOfCollectionUseCase
import com.deepvisiontech.thecomicinator3000.features.comic.domain.usecase.MoveComicsToCollectionUseCase
import com.deepvisiontech.thecomicinator3000.features.comic.presentation.navigation.ComicRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ComicCollectionUiState(
    val isLoading: Boolean = true,
    val comics: List<Comic> = emptyList(),
    val allCollections: List<ComicCollection> = emptyList(),
    val selectedComics: Set<Comic> = emptySet(),
    val searchQuery: String = ""
) {
    val isSearching get() = selectedComics.isNotEmpty()
}

sealed interface ComicCollectionUiEvent {
    data class Error(val message: UiText): ComicCollectionUiEvent
    data class NavigateToComic(val id: String): ComicCollectionUiEvent
}

sealed interface ComicCollectionUiAction {
    data class OnSearchQueryChange(val query: String): ComicCollectionUiAction
    data class MoveSelectedComicsToCollection(val collectionId: Long?): ComicCollectionUiAction
    data class ToggleComicSelection(val comic: Comic): ComicCollectionUiAction

    data class OpenComic(val comicId: String): ComicCollectionUiAction
}

@HiltViewModel
class ComicCollectionViewModel @Inject constructor(
    private val getFilteredComicsOfCollectionUseCase: GetFilteredComicsOfCollectionUseCase,
    private val getAllComicCollectionsUseCase: GetAllComicCollectionsUseCase,
    private val moveComicsToCollectionUseCase: MoveComicsToCollectionUseCase,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val _collectionArgs = savedStateHandle.toRoute<ComicRoute.ComicCollection>()
    private val _collectionId: Long? = _collectionArgs.collectionId

    private val _searchQuery = MutableStateFlow("")
    private val _selectedComics: MutableStateFlow<Set<Comic>> = MutableStateFlow(emptySet())

    private val _uiEvent = MutableSharedFlow<ComicCollectionUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    val uiState: StateFlow<ComicCollectionUiState> = combine(
        getFilteredComicsOfCollectionUseCase(_collectionId, _searchQuery),
        getAllComicCollectionsUseCase(),
        _searchQuery,
        _selectedComics
    ) { comics, collections, searchQuery, selectedComics ->
        ComicCollectionUiState(
            isLoading = false,
            comics = comics,
            allCollections = collections,
            searchQuery = searchQuery,
            selectedComics = selectedComics
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        initialValue = ComicCollectionUiState(isLoading = true)
    )

    private fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    private fun onToggleComicSelection(comic: Comic) {
        _selectedComics.update { current ->
            if (current.contains(comic)) {
                current - comic
            } else {
                current + comic
            }
        }
    }

    private fun onMoveSelectedComicsToCollection(collectionId: Long?) {
        viewModelScope.launch {
            val comicIds = _selectedComics.value.map { it.id }
            val response = moveComicsToCollectionUseCase(comicIds, collectionId)
            when(response) {
                is EvilResponse.Failure -> {
                    _uiEvent.emit(
                        ComicCollectionUiEvent.Error(
                            UiText.StringResource(
                                R.string.comic_collection_error_move_comic
                            )
                        )
                    )
                }
                is EvilResponse.Success<Unit> -> {
                    _selectedComics.value = emptySet()
                }
            }
        }
    }

    private fun onOpenComic(id: String) {
        viewModelScope.launch {
            _uiEvent.emit(ComicCollectionUiEvent.NavigateToComic(id))
        }
    }

    fun onAction(action: ComicCollectionUiAction) {
        when (action) {
            is ComicCollectionUiAction.OnSearchQueryChange -> onSearchQueryChange(action.query)
            is ComicCollectionUiAction.ToggleComicSelection -> onToggleComicSelection(action.comic)
            is ComicCollectionUiAction.MoveSelectedComicsToCollection -> onMoveSelectedComicsToCollection(action.collectionId)
            is ComicCollectionUiAction.OpenComic -> onOpenComic(action.comicId)
        }
    }
}