package com.deepvisiontech.thecomicinator3000.features.comic.domain.usecase

import com.deepvisiontech.thecomicinator3000.core.domain.model.EvilResponse
import com.deepvisiontech.thecomicinator3000.features.comic.domain.model.ComicCollection
import com.deepvisiontech.thecomicinator3000.features.comic.domain.repository.ComicCollectionRepository
import javax.inject.Inject

class DeleteComicCollectionsUseCase @Inject constructor(
    private val comicCollectionRepository: ComicCollectionRepository
) {
    suspend operator fun invoke(collections: List<ComicCollection>): EvilResponse<Unit> {
        return comicCollectionRepository.deleteCollections(collections)
    }
}