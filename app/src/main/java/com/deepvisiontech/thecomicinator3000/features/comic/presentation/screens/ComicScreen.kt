package com.deepvisiontech.thecomicinator3000.features.comic.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.deepvisiontech.thecomicinator3000.R
import com.deepvisiontech.thecomicinator3000.features.comic.presentation.viewmodels.ComicUiEvent
import com.deepvisiontech.thecomicinator3000.features.comic.presentation.viewmodels.ComicViewModel
import me.saket.telephoto.zoomable.coil.ZoomableAsyncImage
import me.saket.telephoto.zoomable.rememberZoomableImageState

@Composable
fun ComicScreen(
    modifier: Modifier = Modifier,
    comicViewModel: ComicViewModel = hiltViewModel(),
    navigateBack: () -> Unit
) {
    val uiState by comicViewModel.uiState.collectAsStateWithLifecycle()
    val uiEvent = comicViewModel.uiEvent
    val snackbarHostState = remember { SnackbarHostState() }

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { uiState.numberOfPages }
    )
    var isZoomedIn by remember { mutableStateOf(false) }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        uiEvent.collect { event ->
            when (event) {
                is ComicUiEvent.Error -> {
                    snackbarHostState.showSnackbar(event.message.asString(context))
                }
            }
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            AnimatedVisibility(
                visible = !isZoomedIn,
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(tween(200))
            ) {
                TopAppBar(
                    title = {
                        Text(
                            text = uiState.comic.displayName,
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = navigateBack) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.global_cd_navigate_back)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                    )
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()) {

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { pageIndex ->

                val zoomState = rememberZoomableImageState()

                if (pagerState.currentPage == pageIndex) {
                    LaunchedEffect(zoomState.zoomableState.zoomFraction) {
                        isZoomedIn = (zoomState.zoomableState.zoomFraction ?: 0f) > 0.05f
                    }
                }

                LaunchedEffect(pagerState.currentPage) {
                    if (pagerState.currentPage != pageIndex) {
                        zoomState.zoomableState.resetZoom(animationSpec = tween(300))
                    }
                }

                ZoomableAsyncImage(
                    state = zoomState,
                    model = uiState.comicPages.getOrNull(pageIndex),
                    contentDescription = stringResource(R.string.comic_cd_comic_page, pageIndex + 1),
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}