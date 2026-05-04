package com.deepvisiontech.thecomicinator3000.features.comic.domain.usecase

import com.deepvisiontech.thecomicinator3000.features.comic.domain.model.ComicCollection
import com.deepvisiontech.thecomicinator3000.features.comic.domain.repository.ComicCollectionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllComicCollectionsUseCase @Inject constructor(
    private val comicCollectionRepository: ComicCollectionRepository
) {
    operator fun invoke(): Flow<List<ComicCollection>> {
        return comicCollectionRepository.getAllComicCollection()
    }
}