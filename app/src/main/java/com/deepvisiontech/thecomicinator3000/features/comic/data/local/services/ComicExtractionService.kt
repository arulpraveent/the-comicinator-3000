package com.deepvisiontech.thecomicinator3000.features.comic.data.local.services

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipInputStream
import javax.inject.Inject
import androidx.core.net.toUri

class ComicExtractionService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private companion object {
        private const val CACHE_DIR_NAME = "extracted_comics"
    }

    suspend fun getComicPages(comicId: String, comicUri: String, recentlyOpenedIds: List<String>): List<Uri> = withContext(Dispatchers.IO) {

        val cacheDir = File(context.cacheDir, CACHE_DIR_NAME).apply {
            if (!exists()) mkdirs()
        }

        val comicFolder = File(cacheDir, comicId).apply {
            if (!exists()) mkdirs()
        }

        if (comicFolder.listFiles()?.isEmpty() == true) {
            extractComicPages(comicUri.toUri(), comicFolder)
        }

        cleanupOldCaches(cacheDir, recentlyOpenedIds)

        return@withContext getSortedPageUris(comicFolder)
    }

    private fun cleanupOldCaches(baseCacheDir: File, recentlyOpenedIds: List<String>) {
        val cachedComicFolders = baseCacheDir.listFiles()?.filter { it.isDirectory } ?: return
        cachedComicFolders.forEach { folder ->
            if (!recentlyOpenedIds.contains(folder.name)) {
                folder.deleteRecursively()
            }
        }
    }

    private fun extractComicPages(uri: Uri, destinationFolder: File) {
        context.contentResolver.openInputStream(uri)?.use { rawStream ->
            ZipInputStream(rawStream).use { zipInputStream ->
                var entry = zipInputStream.nextEntry

                while (entry != null) {
                    if (!entry.isDirectory && isImageFile(entry.name)) {
                        val safeFileName = File(entry.name).name
                        val destinationFile = File(destinationFolder, safeFileName)

                        FileOutputStream(destinationFile).use { outputStream ->
                            zipInputStream.copyTo(outputStream)
                        }
                    }
                    zipInputStream.closeEntry()
                    entry = zipInputStream.nextEntry
                }
            }
        }
    }

    private fun getSortedPageUris(comicFolder: File): List<Uri> {
        val files = comicFolder.listFiles() ?: return emptyList()
        return files.sortedBy { it.name }.map { Uri.fromFile(it) }
    }

    private fun isImageFile(fileName: String): Boolean {
        val extension = fileName.substringAfterLast('.').lowercase()
        return extension in listOf("jpg", "jpeg", "png", "webp", "gif")
    }

    private fun isUriAccessible(context: Context, uri: Uri): Boolean {
        return try {
            context.contentResolver.openFileDescriptor(uri, "r")?.use {
                return true
            }
            false
        } catch (_: Exception) {
            false
        }
    }
}