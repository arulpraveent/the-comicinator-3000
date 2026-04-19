package com.deepvisiontech.thecomicinator3000.features.comic.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.deepvisiontech.thecomicinator3000.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionCreationDialog(
    modifier: Modifier = Modifier,
    onCreateClick: (String) -> Unit,
    onDismissRequest: () -> Unit
) {
    var collectionName by remember { mutableStateOf("") }

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
            OutlinedTextField(
                value = collectionName,
                onValueChange = { collectionName = it },
                label = {
                    Text(
                        stringResource(
                            R.string.comic_library_text_field_label_collection
                        )
                    )
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onCreateClick(collectionName)
                },
                enabled = collectionName.isNotBlank()
            ) {
                Text(
                    stringResource(
                        R.string.global_action_create
                    )
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
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