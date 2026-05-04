package com.deepvisiontech.thecomicinator3000.features.comic.domain.usecase

import com.deepvisiontech.thecomicinator3000.core.domain.model.EvilResponse
import com.deepvisiontech.thecomicinator3000.features.comic.domain.repository.ComicRepository
import javax.inject.Inject

class MoveComicsToCollectionUseCase @Inject constructor(
    private val comicRepository: ComicRepository
) {
    suspend operator fun invoke(
        comicIds: List<String>,
        collectionId: Long?
    ): EvilResponse<Unit> {
        return comicRepository.addComicsToCollection(comicIds,collectionId)
    }
}