package com.deepvisiontech.thecomicinator3000.features.comic.domain.usecase

import com.deepvisiontech.thecomicinator3000.core.data.utils.safeEvilResponseCall
import com.deepvisiontech.thecomicinator3000.core.domain.model.EvilResponse
import com.deepvisiontech.thecomicinator3000.core.domain.repository.StorageAccessRepository
import com.deepvisiontech.thecomicinator3000.features.comic.domain.repository.ComicRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ScanAndSyncComicsUseCase @Inject constructor(
    private val comicRepository: ComicRepository,
    private val storageAccessRepository: StorageAccessRepository
) {
    suspend operator fun invoke(): EvilResponse<Unit> {
        return safeEvilResponseCall(TAG) {
            val storageUri = storageAccessRepository.storageUri.first()
            if (!storageUri.isNullOrBlank()) {
                comicRepository.scanAndSyncComics(storageUri)
            }
        }
    }

    private companion object {
        private const val TAG = "RunComicScanUseCase"
    }
}