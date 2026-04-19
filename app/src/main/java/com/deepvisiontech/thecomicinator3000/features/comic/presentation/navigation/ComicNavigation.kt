package com.deepvisiontech.thecomicinator3000.features.comic.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.deepvisiontech.thecomicinator3000.features.comic.presentation.screens.ComicLibraryScreen

fun NavGraphBuilder.comicGraph() {
    navigation<ComicGraph>(startDestination = ComicRoute.ComicLibrary) {
        composable<ComicRoute.ComicLibrary> {
            ComicLibraryScreen(
                navigateToComicCollection = {}
            )
        }
    }
}