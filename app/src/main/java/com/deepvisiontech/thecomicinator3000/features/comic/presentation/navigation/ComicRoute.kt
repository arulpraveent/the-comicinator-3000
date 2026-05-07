package com.deepvisiontech.thecomicinator3000.features.comic.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface ComicRoute {

    @Serializable
    data object ComicLibraryScreen: ComicRoute

    @Serializable
    data class ComicCollectionScreen(val collectionId: Long?): ComicRoute

    @Serializable
    data class ComicScreen(val comicId: String): ComicRoute
}