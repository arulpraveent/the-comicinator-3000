package com.deepvisiontech.thecomicinator3000.features.comic.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.deepvisiontech.thecomicinator3000.R
import com.deepvisiontech.thecomicinator3000.features.comic.presentation.components.ComicCollectionTopBar
import com.deepvisiontech.thecomicinator3000.features.comic.presentation.components.ComicItemCard
import com.deepvisiontech.thecomicinator3000.features.comic.presentation.components.MoveToCollectionDialog
import com.deepvisiontech.thecomicinator3000.features.comic.presentation.viewmodels.ComicCollectionUiAction
import com.deepvisiontech.thecomicinator3000.features.comic.presentation.viewmodels.ComicCollectionUiEvent
import com.deepvisiontech.thecomicinator3000.features.comic.presentation.viewmodels.ComicCollectionViewModel

@Composable
fun ComicCollectionScreen(
    modifier: Modifier = Modifier,
    comicCollectionViewModel: ComicCollectionViewModel = hiltViewModel(),
    navigateToComic: (id: String) -> Unit
) {
    val uiState by comicCollectionViewModel.uiState.collectAsStateWithLifecycle()
    val uiEvent = comicCollectionViewModel.uiEvent

    val context = LocalContext.current

    val snackbarHostState = remember { SnackbarHostState() }
    var isMoveComicsDialogVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        uiEvent.collect { event ->
            when(event) {
                is ComicCollectionUiEvent.Error -> {
                    snackbarHostState.showSnackbar(event.message.asString(context))
                }
                is ComicCollectionUiEvent.NavigateToComic -> {
                    navigateToComic(event.id)
                }
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            ComicCollectionTopBar(
                title = uiState.activeCollection?.displayName ?: stringResource(R.string.comic_collection_title_uncollected),
                searchBarPlaceHolder = uiState.searchQuery,
                searchQuery = uiState.searchQuery,
                isSelecting = uiState.isSelecting,
                onMoveClick = {
                    isMoveComicsDialogVisible = true
                },
                onSearchQueryChange = { query ->
                    comicCollectionViewModel.onAction(ComicCollectionUiAction.OnSearchQueryChange(query))
                },
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isMoveComicsDialogVisible) {
                MoveToCollectionDialog(
                    comicCollections = uiState.allCollections,
                    onMoveClick = { id ->
                        comicCollectionViewModel.onAction(ComicCollectionUiAction.MoveSelectedComicsToCollection(id))
                        isMoveComicsDialogVisible = false
                    },
                    onDismissRequest = {
                        isMoveComicsDialogVisible = false
                    }
                )
            }
            LazyVerticalGrid(
                modifier = Modifier
                    .fillMaxSize(),
                columns = GridCells.Adaptive(minSize = 128.dp)
            ) {
                items(uiState.comics) { comic ->
                    ComicItemCard(
                        modifier = Modifier.padding(4.dp),
                        title = comic.displayName,
                        imageUri = comic.coverImageUri,
                        dateCreated = comic.lastOpened,
                        isSelected = comic in uiState.selectedComics,
                        onLongClick = {
                            if (!uiState.isSelecting) {
                                comicCollectionViewModel.onAction(
                                    ComicCollectionUiAction.ToggleComicSelection(
                                        comic
                                    )
                                )
                            }
                        },
                        onClick = {
                            if (uiState.isSelecting) {
                                comicCollectionViewModel.onAction(
                                    ComicCollectionUiAction.ToggleComicSelection(
                                        comic
                                    )
                                )
                            } else {
                                comicCollectionViewModel.onAction(
                                    ComicCollectionUiAction.OpenComic(comic.id)
                                )
                            }
                        },
                        placeholderRes = R.drawable.comic_cover_placeholder
                    )
                }
            }
        }
    }
}