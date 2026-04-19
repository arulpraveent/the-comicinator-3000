package com.deepvisiontech.thecomicinator3000.features.comic.domain.usecase

import com.deepvisiontech.thecomicinator3000.core.domain.model.EvilResponse
import com.deepvisiontech.thecomicinator3000.features.comic.domain.model.Comic
import com.deepvisiontech.thecomicinator3000.features.comic.domain.repository.ComicRepository
import javax.inject.Inject

class DeleteComicsUseCase @Inject constructor(
    private val comicRepository: ComicRepository
) {
    suspend operator fun invoke(comics: List<Comic>): EvilResponse<Unit> {
        return comicRepository.deleteComics(comics)
    }
}