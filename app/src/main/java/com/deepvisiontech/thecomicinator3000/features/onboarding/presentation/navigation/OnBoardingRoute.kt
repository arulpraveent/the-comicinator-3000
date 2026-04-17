package com.deepvisiontech.thecomicinator3000.features.onboarding.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface OnBoardingRoute {
    @Serializable
    data object OnBoardingScreen: OnBoardingRoute
}