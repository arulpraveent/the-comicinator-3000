package com.deepvisiontech.thecomicinator3000.features.comic.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface ComicRoute {

    @Serializable
    data object ComicLibrary: ComicRoute
}