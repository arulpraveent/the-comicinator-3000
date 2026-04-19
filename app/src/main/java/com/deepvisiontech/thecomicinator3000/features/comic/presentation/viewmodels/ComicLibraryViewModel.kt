package com.deepvisiontech.thecomicinator3000.features.comic.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deepvisiontech.thecomicinator3000.core.domain.model.DateFilterRange
import com.deepvisiontech.thecomicinator3000.core.domain.model.EvilResponse
import com.deepvisiontech.thecomicinator3000.core.domain.model.ListSortOrder
import com.deepvisiontech.thecomicinator3000.core.domain.model.UiText
import com.deepvisiontech.thecomicinator3000.features.comic.domain.model.ComicCollection
import com.deepvisiontech.thecomicinator3000.features.comic.domain.usecase.DeleteComicCollectionsUseCase
import com.deepvisiontech.thecomicinator3000.features.comic.domain.usecase.GetFilteredComicCollectionsUseCase
import com.deepvisiontech.thecomicinator3000.features.comic.domain.usecase.InsertCollectionUseCase
import com.deepvisiontech.thecomicinator3000.features.comic.domain.usecase.ScanAndSyncComicsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ComicLibraryUiState(
    val comicCollections: List<ComicCollection> = emptyList(),
    val isLoading: Boolean = true,
    val searchQuery: String = "",
    val dateFilterRange: DateFilterRange = DateFilterRange(null,null),
    val listSortOrder: ListSortOrder = ListSortOrder.BY_TIME_DESC,
    val selectedCollections: Set<ComicCollection> = emptySet()
) {
    val isSelecting: Boolean get() = selectedCollections.isNotEmpty()
}

sealed interface ComicLibraryUiEvent {
    data class Error(val message: UiText): ComicLibraryUiEvent
    data class NavigateToComicCollection(val id: Long?): ComicLibraryUiEvent
}

sealed interface ComicLibraryUiAction {
    data class OnSearchQueryUpdate(val queryString: String): ComicLibraryUiAction
    data class OnSortOrderUpdate(val sortOrder: ListSortOrder): ComicLibraryUiAction
    data class OnUpdateDateFilterRange(val dateFilterRange: DateFilterRange): ComicLibraryUiAction
    data object OnClearDateFilterRange: ComicLibraryUiAction

    data object OnClearSearchQuery: ComicLibraryUiAction
    data class OnCreateNewComicCollection(val collectionName: String): ComicLibraryUiAction
    data class OnCollectionToggled(val collection: ComicCollection): ComicLibraryUiAction
    data object OnDelectCollections: ComicLibraryUiAction
    data class OnCollectionOpen(val collectionId: Long?): ComicLibraryUiAction
}

@HiltViewModel
class ComicLibraryViewModel @Inject constructor(
    private val getFilteredComicCollectionsUseCase: GetFilteredComicCollectionsUseCase,
    private val deleteComicCollectionsUseCase: DeleteComicCollectionsUseCase,
    private val scanAndSyncComicsUseCase: ScanAndSyncComicsUseCase,
    private val insertCollectionUseCase: InsertCollectionUseCase
): ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _dateFilterRange = MutableStateFlow(DateFilterRange(null,null))
    private val _listSortOrder = MutableStateFlow(ListSortOrder.BY_TIME_DESC)
    private val _selectedCollections = MutableStateFlow<Set<ComicCollection>>(emptySet())

    val uiState: StateFlow<ComicLibraryUiState> = combine(
        getFilteredComicCollectionsUseCase(
            _searchQuery,
            _listSortOrder,
            _dateFilterRange,
        ),
        _searchQuery,
        _listSortOrder,
        _dateFilterRange,
        _selectedCollections
    ) { filteredCollections, searchQuery, listSortOrder, dateFilterRange, selectedCollections ->
        ComicLibraryUiState(
            filteredCollections,
            false,
            searchQuery,
            dateFilterRange,
            listSortOrder,
            selectedCollections
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        ComicLibraryUiState(isLoading = true)
    )

    private val _uiEvent = MutableSharedFlow<ComicLibraryUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            scanAndSyncComicsUseCase()
        }
    }

    private fun onClearDateFilterRange() {
        _dateFilterRange.value = DateFilterRange(null,null)
    }

    private fun onCollectionOpen(collectionId: Long?) {
        viewModelScope.launch {
            _uiEvent.emit(ComicLibraryUiEvent.NavigateToComicCollection(collectionId))
        }
    }

    private fun onCollectionToggled(collection: ComicCollection) {
        _selectedCollections.value = if (collection in _selectedCollections.value) {
            _selectedCollections.value - collection
        } else {
            _selectedCollections.value + collection
        }
    }

    private fun onCreateNewCollection(collectionName: String) {
        viewModelScope.launch {
            val response = insertCollectionUseCase(collectionName)

            if (response is EvilResponse.Failure) {
                _uiEvent.emit(
                    ComicLibraryUiEvent.Error(
                        UiText.DynamicString("")
                    )
                )
            }
        }
    }

    private fun onDeleteCollections() {
        viewModelScope.launch {
            val response = deleteComicCollectionsUseCase(_selectedCollections.value.toList())

            when(response) {
                is EvilResponse.Failure -> {
                    _uiEvent.emit(
                        ComicLibraryUiEvent.Error(
                            UiText.DynamicString("")
                        )
                    )
                }
                is EvilResponse.Success<Unit> -> {
                    _selectedCollections.value = emptySet()
                }
            }
        }
    }

    private fun onSearchQueryUpdate(query: String) {
        _searchQuery.value = query
    }

    private fun onSortOrderUpdate(listSortOrder: ListSortOrder) {
        _listSortOrder.value = listSortOrder
    }

    private fun onUpdateDateFilterRange(dateFilterRange: DateFilterRange) {
        _dateFilterRange.value = dateFilterRange
    }

    private fun onClearSearchQuery() {
        _searchQuery.value = ""
    }

    fun onAction(action: ComicLibraryUiAction) {
        when (action) {
            ComicLibraryUiAction.OnClearDateFilterRange -> onClearDateFilterRange()
            is ComicLibraryUiAction.OnCollectionOpen -> onCollectionOpen(action.collectionId)
            is ComicLibraryUiAction.OnCollectionToggled -> onCollectionToggled(action.collection)
            is ComicLibraryUiAction.OnCreateNewComicCollection -> onCreateNewCollection(action.collectionName)
            ComicLibraryUiAction.OnDelectCollections -> onDeleteCollections()
            is ComicLibraryUiAction.OnSearchQueryUpdate -> onSearchQueryUpdate(action.queryString)
            is ComicLibraryUiAction.OnSortOrderUpdate -> onSortOrderUpdate(action.sortOrder)
            is ComicLibraryUiAction.OnUpdateDateFilterRange -> onUpdateDateFilterRange(action.dateFilterRange)
            ComicLibraryUiAction.OnClearSearchQuery -> onClearSearchQuery()
        }
    }
}