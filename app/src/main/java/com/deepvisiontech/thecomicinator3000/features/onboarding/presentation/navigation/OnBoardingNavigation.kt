package com.deepvisiontech.thecomicinator3000.features.onboarding.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.deepvisiontech.thecomicinator3000.features.onboarding.presentation.screens.OnBoardingScreen

fun NavGraphBuilder.onBoardingGraph(
    navigateToComicGraph: () -> Unit
) {
    navigation<OnBoardingGraph>(startDestination = OnBoardingRoute.OnBoardingScreen) {
        composable<OnBoardingRoute.OnBoardingScreen> {
            OnBoardingScreen(
                onNavigateToComicGraph = navigateToComicGraph
            )
        }
    }
}