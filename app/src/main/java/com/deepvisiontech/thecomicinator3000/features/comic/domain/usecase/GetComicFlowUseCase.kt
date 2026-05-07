package com.deepvisiontech.thecomicinator3000.features.comic.domain.usecase

import com.deepvisiontech.thecomicinator3000.features.comic.domain.model.Comic
import com.deepvisiontech.thecomicinator3000.features.comic.domain.repository.ComicRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetComicFlowUseCase @Inject constructor(
    private val comicRepository: ComicRepository
) {
    operator fun invoke(id: String): Flow<Comic> {
        return comicRepository.getComicFlow(id)
    }
}