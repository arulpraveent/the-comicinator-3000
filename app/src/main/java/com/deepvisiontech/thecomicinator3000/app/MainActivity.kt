package com.deepvisiontech.thecomicinator3000.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.deepvisiontech.thecomicinator3000.core.ui.theme.TheComicinator3000Theme
import com.deepvisiontech.thecomicinator3000.features.onboarding.presentation.screens.OnBoardingScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TheComicinator3000Theme {
                OnBoardingScreen(
                    onNavigateToLibrary = {}
                )
            }
        }
    }
}