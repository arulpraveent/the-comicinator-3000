package com.deepvisiontech.thecomicinator3000.features.comic.data.local.workers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.deepvisiontech.thecomicinator3000.features.comic.data.local.dao.ComicDao
import com.deepvisiontech.thecomicinator3000.features.comic.domain.model.ComicMetadata
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipInputStream

@HiltWorker
class ComicMetadataScannerWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val comicDao: ComicDao
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val allComics = comicDao.getAllComicsWithoutCover()
        val coverFolder = File(context.cacheDir, "covers").apply {
            if (!exists()) mkdirs()
        }

        allComics.forEach { comicEntity ->
            try {
                val comicUri = comicEntity.fileUri.toUri()
                if (!isUriAccessible(context, comicUri)) return@forEach

                var comicCoverBitmap: Bitmap? = null
                var comicMetaXmlBytes: ByteArray? = null

                context.contentResolver.openInputStream(comicUri)?.use { rawStream ->
                    ZipInputStream(rawStream).use { zipInputStream ->
                        var entry = zipInputStream.nextEntry
                        while (entry != null) {
                            val fileName = entry.name

                            if (entry.isDirectory || fileName.contains("__MACOSX") || fileName.startsWith(".")) {
                                zipInputStream.closeEntry()
                                entry = zipInputStream.nextEntry
                                continue
                            }

                            if (comicMetaXmlBytes == null && fileName.equals("ComicInfo.xml", ignoreCase = true)) {
                                comicMetaXmlBytes = zipInputStream.readBytes()
                            }

                            if (comicCoverBitmap == null && isImageFile(fileName)) {
                                comicCoverBitmap = BitmapFactory.decodeStream(zipInputStream)
                            }

                            if (comicCoverBitmap != null && comicMetaXmlBytes != null) {
                                break
                            }

                            zipInputStream.closeEntry()
                            entry = zipInputStream.nextEntry
                        }
                    }
                }

                comicCoverBitmap?.let { bitmap ->
                    val coverFile = File(coverFolder, "${comicEntity.id}.jpg")
                    FileOutputStream(coverFile).use { out ->
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out)
                    }
                    comicDao.updateCover(comicEntity.id, coverFile.absolutePath)
                }

                comicMetaXmlBytes?.let { xmlBytes ->
                    val metadata = parseXml(xmlBytes)
                    comicDao.updateMetadata(
                        comicId = comicEntity.id,
                        title = metadata.title,
                        series = metadata.series,
                        number = metadata.number,
                        genre = metadata.genre,
                        year = metadata.year
                    )
                }

            } catch (e: Exception) {
                Log.e(TAG, "Failed to process ${comicEntity.displayName}", e)
            }
        }
        return@withContext Result.success()
    }

    private fun parseXml(xmlBytes: ByteArray): ComicMetadata {
        val parser = XmlPullParserFactory.newInstance().newPullParser()
        parser.setInput(ByteArrayInputStream(xmlBytes), null)

        var eventType = parser.eventType
        var title: String? = null
        var series: String? = null
        var number: String? = null
        var genre: String? = null
        var year: String? = null

        fun safeText(): String? {
            return try {
                parser.nextText()?.trim()?.takeIf { it.isNotEmpty() }
            } catch (_: Exception) {
                null
            }
        }

        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                when (parser.name?.lowercase()?.trim()) {
                    "title" -> title = safeText()
                    "series", "seriessort" -> if (series == null) series = safeText()
                    "number" -> number = safeText()
                    "genre" -> genre = safeText()
                    "year" -> year = safeText()
                }
            }
            eventType = try {
                parser.next()
            } catch (_: Exception) {
                break
            }
        }

        return ComicMetadata(title, series, number, genre, year)
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

    private companion object {
        private const val TAG = "ComicMetadataScannerWorker"
    }
}
