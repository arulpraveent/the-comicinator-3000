package com.deepvisiontech.thecomicinator3000.features.comic.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.deepvisiontech.thecomicinator3000.R
import com.deepvisiontech.thecomicinator3000.features.comic.domain.model.ComicCollection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoveToCollectionDialog(
    modifier: Modifier = Modifier,
    comicCollections: List<ComicCollection>,
    onMoveClick: (collectionId: Long?) -> Unit,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        modifier = modifier,
        icon = {},
        title = {
            Text(
                stringResource(
                    R.string.comic_library_dialog_title_create_collection
                )
            )
        },
        text = {
            LazyColumn {
                item {
                    Text(
                        text = stringResource(R.string.comic_library_card_uncollected_title),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onMoveClick(null)
                            }
                            .padding(vertical = 16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                items(comicCollections) { collection ->
                    Text(
                        text = collection.displayName,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onMoveClick(collection.id)
                            }
                            .padding(vertical = 16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {},
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(
                    stringResource(
                        R.string.global_action_dismiss
                    )
                )
            }
        }
    )
}