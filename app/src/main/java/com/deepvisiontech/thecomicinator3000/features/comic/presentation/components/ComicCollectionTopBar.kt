package com.deepvisiontech.thecomicinator3000.features.comic.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DriveFileMove
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.deepvisiontech.thecomicinator3000.R

@Composable
fun ComicCollectionTopBar(
    modifier: Modifier = Modifier,
    title: String,
    searchBarPlaceHolder: String,
    searchQuery: String,
    isSelecting: Boolean,
    onMoveClick: () -> Unit,
    onSearchQueryChange: (query: String) -> Unit,
) {

    val isSearching = rememberSaveable { mutableStateOf(false) }

    TopAppBar(
        modifier = modifier,
        title = {
            AnimatedVisibility (
                visible = isSearching.value
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth(),
                    leadingIcon = {
                        IconButton(
                            onClick = {
                                isSearching.value = false
                            }
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                stringResource(R.string.global_cd_navigate_back)
                            )
                        }
                    },
                    placeholder = {
                        Text(text = searchBarPlaceHolder)
                    },
                    value = searchQuery,
                    onValueChange = {
                        onSearchQueryChange(it)
                    },
                    maxLines = 1,
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                onSearchQueryChange("")
                            }
                        ) {
                            Icon(
                                Icons.Default.Clear,
                                stringResource(R.string.global_cd_clear_text)
                            )
                        }
                    },
                    textStyle = MaterialTheme.typography.bodySmall,
                    singleLine = true
                )
            }
            AnimatedVisibility (
                visible = !isSearching.value
            ) {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        actions = {
            Row {
                AnimatedVisibility (
                    isSelecting
                ) {
                    IconButton(onClick = {
                        onMoveClick()
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.DriveFileMove,
                            stringResource(R.string.global_cd_move)
                        )
                    }
                }

                AnimatedVisibility (
                    !isSelecting && !isSearching.value
                ) {
                    Row {
                        IconButton( onClick = {
                            isSearching.value = true
                        }
                        ) {
                            Icon(
                                Icons.Default.Search,
                                stringResource(R.string.global_cd_search)
                            )
                        }
                    }
                }
            }
        }
    )
}