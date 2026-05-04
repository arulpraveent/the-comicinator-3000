package com.deepvisiontech.thecomicinator3000.features.comic.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.deepvisiontech.thecomicinator3000.features.comic.presentation.screens.ComicCollectionScreen
import com.deepvisiontech.thecomicinator3000.features.comic.presentation.screens.ComicLibraryScreen

fun NavGraphBuilder.comicGraph(
    navHostController: NavHostController
) {
    navigation<ComicGraph>(startDestination = ComicRoute.ComicLibraryScreen) {
        composable<ComicRoute.ComicLibraryScreen> {
            ComicLibraryScreen(
                navigateToComicCollection = { id ->
                    navHostController.navigate(ComicRoute.ComicCollectionScreen(id))
                }
            )
        }

        composable<ComicRoute.ComicCollectionScreen> {
            ComicCollectionScreen(
                navigateToComic = { id ->

                }
            )
        }
    }
}