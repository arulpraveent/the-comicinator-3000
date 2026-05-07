package com.deepvisiontech.thecomicinator3000.features.comic.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.deepvisiontech.thecomicinator3000.R
import com.deepvisiontech.thecomicinator3000.features.comic.presentation.components.CollectionCreationDialog
import com.deepvisiontech.thecomicinator3000.features.comic.presentation.components.ComicItemCard
import com.deepvisiontech.thecomicinator3000.features.comic.presentation.components.ComicLibraryTopBar
import com.deepvisiontech.thecomicinator3000.features.comic.presentation.components.DateRangePickerDialog
import com.deepvisiontech.thecomicinator3000.features.comic.presentation.viewmodels.ComicLibraryUiAction
import com.deepvisiontech.thecomicinator3000.features.comic.presentation.viewmodels.ComicLibraryUiEvent
import com.deepvisiontech.thecomicinator3000.features.comic.presentation.viewmodels.ComicLibraryViewModel

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ComicLibraryScreen(
    modifier: Modifier = Modifier,
    comicLibraryViewModel: ComicLibraryViewModel = hiltViewModel(),
    navigateToComicCollection: (id: Long?) -> Unit
) {

    val uiState by comicLibraryViewModel.uiState.collectAsStateWithLifecycle()
    val uiEvent = comicLibraryViewModel.uiEvent

    val snackbarHostState by remember {  mutableStateOf(SnackbarHostState()) }
    var isCollectionCreationDialogVisible by remember { mutableStateOf(false) }
    var isDateFilterShown by remember { mutableStateOf(false) }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        uiEvent.collect { event ->
            when(event) {
                is ComicLibraryUiEvent.Error -> {
                    snackbarHostState.showSnackbar(event.message.asString(context))
                }
                is ComicLibraryUiEvent.NavigateToComicCollection -> {
                    navigateToComicCollection(event.id)
                }
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            ComicLibraryTopBar(
                title = stringResource(R.string.comic_library_title),
                searchBarPlaceHolder = stringResource(R.string.comic_library_search_placeholder),
                searchQuery = uiState.searchQuery,
                isSelecting = uiState.isSelecting,
                onDeleteClick = {
                    comicLibraryViewModel.onAction(ComicLibraryUiAction.OnDelectCollections)
                },
                onDateFilterClick = {
                    isDateFilterShown = true
                },
                onSortOrderChange = { order ->
                    comicLibraryViewModel.onAction(ComicLibraryUiAction.OnSortOrderUpdate(order))
                },
                onSearchQueryChange = { query ->
                    comicLibraryViewModel.onAction(ComicLibraryUiAction.OnSearchQueryUpdate(query))
                }
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    isCollectionCreationDialogVisible = true
                },
            ) {
                Icon(
                    Icons.Default.Add,
                    stringResource(R.string.global_cd_create)
                )
            }
        }
    ) { paddingValues ->

        if (isCollectionCreationDialogVisible) {
            CollectionCreationDialog(
                onCreateClick = { collectionName ->
                    comicLibraryViewModel.onAction(ComicLibraryUiAction.OnCreateNewComicCollection(collectionName))
                    isCollectionCreationDialogVisible = false
                },
                onDismissRequest = {
                    isCollectionCreationDialogVisible = false
                }
            )
        }

        if (isDateFilterShown) {
            DateRangePickerDialog(
                onDateRangeSelected = { dateFilterRange ->
                    comicLibraryViewModel.onAction(ComicLibraryUiAction.OnUpdateDateFilterRange(dateFilterRange))
                    isDateFilterShown = false
                },
                onDismiss = {
                    isDateFilterShown = false
                }
            )
        }

        if (uiState.isLoading) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                ContainedLoadingIndicator()
            }
        } else {
            LazyVerticalGrid(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                columns = GridCells.Adaptive(minSize = 128.dp)
            ) {
                if (!uiState.isSelecting) {
                    item {
                        ComicItemCard(
                            modifier = Modifier.padding(8.dp),
                            title = stringResource(R.string.comic_library_card_uncollected_title),
                            dateCreated = System.currentTimeMillis(),
                            isSelected = false,
                            onLongClick = {
                            },
                            onClick = {
                                comicLibraryViewModel.onAction(
                                    ComicLibraryUiAction.OnCollectionOpen(null)
                                )
                            },
                            placeholderRes = R.drawable.collection_placeholder
                        )
                    }
                }
                items(uiState.comicCollections) { collection ->
                    ComicItemCard(
                        modifier = Modifier.padding(8.dp),
                        title = collection.displayName,
                        dateCreated = collection.timeCreated,
                        onLongClick = {
                            if (!uiState.isSelecting) {
                                comicLibraryViewModel.onAction(
                                    ComicLibraryUiAction.OnCollectionToggled(collection)
                                )
                            }
                        },
                        isSelected = collection in uiState.selectedCollections,
                        onClick = {
                            if (uiState.isSelecting) {
                                comicLibraryViewModel.onAction(
                                    ComicLibraryUiAction.OnCollectionToggled(collection)
                                )
                            } else {
                                comicLibraryViewModel.onAction(
                                    ComicLibraryUiAction.OnCollectionOpen(collection.id)
                                )
                            }
                        },
                        placeholderRes = R.drawable.collection_placeholder
                    )

                }
            }
        }
    }
}