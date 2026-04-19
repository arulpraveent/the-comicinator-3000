package com.deepvisiontech.thecomicinator3000.features.comic.domain.usecase

import com.deepvisiontech.thecomicinator3000.core.domain.model.DateFilterRange
import com.deepvisiontech.thecomicinator3000.core.domain.model.ListSortOrder
import com.deepvisiontech.thecomicinator3000.features.comic.domain.model.Comic
import com.deepvisiontech.thecomicinator3000.features.comic.domain.repository.ComicRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetFilteredComicsOfCollectionUseCase @Inject constructor(
    private val comicRepository: ComicRepository
) {
    operator fun invoke(
        collectionId: Long?,
        searchQuery: Flow<String>
    ): Flow<List<Comic>> {
        val comicsFlow = if (collectionId == null) {
            comicRepository.getAllUncollectedComicsFlow()
        } else {
            comicRepository.getAllComicsOfCollectionFlow(collectionId)
        }

        return combine(
            searchQuery,
            comicsFlow
        ) { query, comics ->
            comics.filter { it.displayName.contains(query, ignoreCase = true) }
        }
    }
}