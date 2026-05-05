package com.deepvisiontech.thecomicinator3000.features.comic.domain.usecase

import android.net.Uri
import com.deepvisiontech.thecomicinator3000.core.domain.model.EvilResponse
import com.deepvisiontech.thecomicinator3000.features.comic.domain.model.Comic
import com.deepvisiontech.thecomicinator3000.features.comic.domain.repository.ComicRepository
import javax.inject.Inject

class GetComicPagesUseCase @Inject constructor(
    private val comicRepository: ComicRepository
) {
    suspend operator fun invoke(comic: Comic): EvilResponse<List<Uri>> {
        return comicRepository.getComicPages(comic)
    }
}