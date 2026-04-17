package com.deepvisiontech.thecomicinator3000.app

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.deepvisiontech.thecomicinator3000.features.onboarding.presentation.navigation.OnBoardingGraph
import com.deepvisiontech.thecomicinator3000.features.onboarding.presentation.navigation.onBoardingGraph

@Composable
fun AppScreen() {
    val navHostController = rememberNavController()

    NavHost(
        navController = navHostController,
        startDestination = OnBoardingGraph
    ) {
        onBoardingGraph(
            navigateToLibraryGraph = {}
        )
    }
}