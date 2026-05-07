package com.deepvisiontech.thecomicinator3000.features.comic.data.repository

import android.content.Context
import android.net.Uri
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.deepvisiontech.thecomicinator3000.core.data.utils.safeEvilResponseCall
import com.deepvisiontech.thecomicinator3000.core.domain.model.EvilResponse
import com.deepvisiontech.thecomicinator3000.features.comic.data.local.dao.ComicDao
import com.deepvisiontech.thecomicinator3000.features.comic.data.local.mappers.toComic
import com.deepvisiontech.thecomicinator3000.features.comic.data.local.mappers.toEntity
import com.deepvisiontech.thecomicinator3000.features.comic.data.local.services.ComicExtractionService
import com.deepvisiontech.thecomicinator3000.features.comic.data.local.services.ComicScannerService
import com.deepvisiontech.thecomicinator3000.features.comic.data.local.workers.ComicMetadataScannerWorker
import com.deepvisiontech.thecomicinator3000.features.comic.domain.model.Comic
import com.deepvisiontech.thecomicinator3000.features.comic.domain.repository.ComicRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ComicRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val comicScannerService: ComicScannerService,
    private val comicExtractionService: ComicExtractionService,
    private val comicDao: ComicDao
): ComicRepository {
    override suspend fun scanAndSyncComics(uriString: String): EvilResponse<Unit> {
        return safeEvilResponseCall(TAG) {
            val scannedComics = comicScannerService.scanForComics(uriString)
            comicDao.insertComics(scannedComics)

            val validIds = scannedComics.map { it.id }
            comicDao.deleteMissingComics(validIds)

            triggerMetadataScanner()
        }
    }

    private fun triggerMetadataScanner() {
        val constraints = Constraints.Builder()
            .build()

        val workRequest = OneTimeWorkRequestBuilder<ComicMetadataScannerWorker>()
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                "ScanComicMetadata",
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
    }

    override fun getAllComicsFlow(): Flow<List<Comic>> {
        return comicDao.getAllComicsWithMetadataFlow().map { entities -> entities.map { it.toComic() } }
    }

    override fun getComicFlow(id: String): Flow<Comic> {
        return comicDao.getComicFlow(id).map { entity -> entity.toComic() }
    }

    override suspend fun getComicPages(comic: Comic): EvilResponse<List<Uri>> {
        return safeEvilResponseCall(TAG) {
            val recentComicsIdList = comicDao.getRecentComicsWithMetadataFlow().first().map { it.comic.id }
            comicExtractionService.getComicPages(comic.id, comic.fileUri, recentComicsIdList)
        }
    }

    override fun getAllComicsOfCollectionFlow(id: Long): Flow<List<Comic>> {
        return comicDao.getAllComicsOfCollectionWithMetadataFlow(id).map { entities -> entities.map { it.toComic() } }
    }

    override fun getAllUncollectedComicsFlow(): Flow<List<Comic>> {
        return comicDao.getAllUncollectedComics().map { entities -> entities.map { it.toComic() } }
    }

    override suspend fun addComicsToCollection(
        comicIds: List<String>,
        collectionId: Long?
    ): EvilResponse<Unit> {
        return safeEvilResponseCall(TAG) {
            comicDao.addComicsToCollection(comicIds, collectionId)
        }
    }

    override suspend fun insertComics(comics: List<Comic>): EvilResponse<Unit> {
        return safeEvilResponseCall(TAG) {
            comicDao.insertComics(comics.map { it.toEntity() })
        }
    }

    override suspend fun deleteComics(comics: List<Comic>): EvilResponse<Unit> {
        return safeEvilResponseCall(TAG) {
            comicDao.deleteComics(comics.map { it.toEntity() })
        }
    }

    override suspend fun updateLastOpened(
        comicId: String,
        timeStamp: Long
    ): EvilResponse<Unit> {
        return safeEvilResponseCall(TAG) {
            comicDao.updateLastOpened(comicId, timeStamp)
        }
    }

    private companion object {
        private const val TAG = "ComicRepositoryImpl"
    }
}