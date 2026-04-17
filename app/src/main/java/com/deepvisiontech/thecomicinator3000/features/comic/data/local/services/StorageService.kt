package com.deepvisiontech.thecomicinator3000.features.comic.data.local.services

import android.content.Context
import android.provider.DocumentsContract
import androidx.core.net.toUri
import com.deepvisiontech.thecomicinator3000.features.comic.domain.model.Comic
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class StorageService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend fun scanForComics(folderUriString: String): List<Comic> = withContext(Dispatchers.IO) {
        val folderUri = folderUriString.toUri()
        val comicsList = mutableListOf<Comic>()

        val folderDocId = DocumentsContract.getTreeDocumentId(folderUri)

        val childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(
            folderUri,
            folderDocId
        )

        val projection = arrayOf(
            DocumentsContract.Document.COLUMN_DOCUMENT_ID,
            DocumentsContract.Document.COLUMN_DISPLAY_NAME,
            DocumentsContract.Document.COLUMN_SIZE
        )

        context.contentResolver.query(
            childrenUri,
            projection,
            null,
            null,
            null
        )?.use { cursor ->

            val idIndex = cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DOCUMENT_ID)
            val nameIndex = cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DISPLAY_NAME)
            val sizeIndex = cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_SIZE)

            while (cursor.moveToNext()) {
                val name = cursor.getString(nameIndex) ?: continue

                if (name.endsWith(".cbz", ignoreCase = true)) {
                    val docId = cursor.getString(idIndex)
                    val size = cursor.getLong(sizeIndex)

                    val fileUri = DocumentsContract.buildDocumentUriUsingTree(folderUri, docId)

                    comicsList.add(
                        Comic(
                            id = docId,
                            displayName = name,
                            fileUri = fileUri.toString(),
                            fileSize = size,
                            lastOpened = System.currentTimeMillis()
                        )
                    )
                }
            }
        }
        return@withContext comicsList
    }
}