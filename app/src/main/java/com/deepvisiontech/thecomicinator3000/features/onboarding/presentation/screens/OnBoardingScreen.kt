package com.deepvisiontech.thecomicinator3000.features.onboarding.presentation.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.deepvisiontech.thecomicinator3000.R
import com.deepvisiontech.thecomicinator3000.features.onboarding.presentation.viewmodels.OnBoardingScreenAction
import com.deepvisiontech.thecomicinator3000.features.onboarding.presentation.viewmodels.OnBoardingScreenEvent
import com.deepvisiontech.thecomicinator3000.features.onboarding.presentation.viewmodels.OnBoardingViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun OnBoardingScreen(
    modifier: Modifier = Modifier,
    onBoardingViewModel: OnBoardingViewModel = hiltViewModel(),
    onNavigateToComicGraph: () -> Unit
) {
    val uiState by onBoardingViewModel.uiState.collectAsState()
    val uiEvent = onBoardingViewModel.uiEvent

    val snackbarHostState by remember { mutableStateOf(SnackbarHostState()) }
    val context = LocalContext.current

    val folderLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocumentTree()
    ) { uri: Uri? ->
        uri?.let {
            val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION

            context.contentResolver.takePersistableUriPermission(it, takeFlags)

            onBoardingViewModel.onAction(OnBoardingScreenAction.OnAccessGranted(it.toString()))
        }
    }

    LaunchedEffect(Unit) {
        uiEvent.collect { event ->
            when (event) {
                is OnBoardingScreenEvent.Error -> {
                    snackbarHostState.showSnackbar(
                        message = event.message.asString(context)
                    )
                }
                OnBoardingScreenEvent.NavigateToLibrary -> {
                    onNavigateToComicGraph()
                }
            }
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(R.string.global_app_title),
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            )
        },
        content = { paddingValues ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                if (!uiState.isLoading && !uiState.isPermissionGranted) {
                    OutlinedCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.onboarding_desc_storage_access),
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )

                            ElevatedButton(
                                onClick = {
                                    folderLauncher.launch(null)
                                }
                            ) {
                                Text(
                                    text = stringResource(R.string.onboarding_button_label_select_folder)
                                )
                            }
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        ContainedLoadingIndicator()
                    }
                }
            }
        }
    )
}