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
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallExtendedFloatingActionButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.deepvisiontech.thecomicinator3000.R
import com.deepvisiontech.thecomicinator3000.features.comic.presentation.components.CollectionCreationDialog
import com.deepvisiontech.thecomicinator3000.features.comic.presentation.components.ComicCollectionCard
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
    navigateToComicCollection: () -> Unit
) {

    val uiState by comicLibraryViewModel.uiState.collectAsStateWithLifecycle()
    val uiEvent = comicLibraryViewModel.uiEvent

    val snackbarHostState = SnackbarHostState()
    var isCollectionCreationDialogVisible by remember { mutableStateOf(false) }
    var isDateFilterShown by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        uiEvent.collect { event ->
            when(event) {
                is ComicLibraryUiEvent.Error -> {
                    snackbarHostState.showSnackbar(event.message.toString())
                }
                is ComicLibraryUiEvent.NavigateToComicCollection -> {
                    navigateToComicCollection()
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
            SmallExtendedFloatingActionButton(
                text = {
                    Text(
                        stringResource(R.string.comic_library_button_create_collection)
                    )
                },
                icon = {
                    Icon(
                        Icons.Default.Add,
                        null
                    )
                },
                onClick = {
                    isCollectionCreationDialogVisible = true
                }
            )
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
                        ComicCollectionCard(
                            title = "Uncollected",
                            dateCreated = System.currentTimeMillis(),
                            onLongClick = {
                            },
                            onClick = {
                                comicLibraryViewModel.onAction(
                                    ComicLibraryUiAction.OnCollectionOpen(null)
                                )
                            }
                        )
                    }
                }
                items(uiState.comicCollections) { collection ->
                    ComicCollectionCard(
                        title = collection.displayName,
                        dateCreated = collection.timeCreated,
                        onLongClick = {
                            if (!uiState.isSelecting) {
                                comicLibraryViewModel.onAction(
                                    ComicLibraryUiAction.OnCollectionToggled(collection)
                                )
                            }
                        },
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
                        }
                    )

                }
            }
        }
    }
}