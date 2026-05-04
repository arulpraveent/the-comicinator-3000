package com.deepvisiontech.thecomicinator3000.features.comic.domain.usecase

import com.deepvisiontech.thecomicinator3000.core.domain.model.DateFilterRange
import com.deepvisiontech.thecomicinator3000.core.domain.model.ListSortOrder
import com.deepvisiontech.thecomicinator3000.features.comic.domain.model.ComicCollection
import com.deepvisiontech.thecomicinator3000.features.comic.domain.repository.ComicCollectionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetFilteredComicCollectionsUseCase @Inject constructor(
    private val comicCollectionRepository: ComicCollectionRepository
) {
    operator fun invoke(
        searchQuery: Flow<String>,
        listSortOrder: Flow<ListSortOrder>,
        dateFilterRange: Flow<DateFilterRange>,
    ): Flow<List<ComicCollection>> {
        return combine(
            searchQuery,
            listSortOrder,
            dateFilterRange,
            comicCollectionRepository.getAllComicCollection()
        ) { searchQuery, listSortOrder, dateFilterRange, collection ->
            val searchedCollection = if (searchQuery.isNotBlank()) {
                collection.filter { it.displayName.contains(searchQuery, ignoreCase = true) }
            } else {
                collection
            }

            val sortedCollection = when(listSortOrder) {
                ListSortOrder.BY_TIME_ASC -> {
                    searchedCollection.sortedBy { it.timeCreated }
                }
                ListSortOrder.BY_TIME_DESC -> {
                    searchedCollection.sortedByDescending { it.timeCreated }
                }
                ListSortOrder.BY_NAME_ASC -> {
                    searchedCollection.sortedBy { it.displayName }
                }
                ListSortOrder.BY_NAME_DESC -> searchedCollection.sortedByDescending { it.displayName }
            }

            val dateFilteredCollection = if (dateFilterRange.startDate != null && dateFilterRange.endDate != null) {
                val startMillisEpoch = dateFilterRange.startDate
                val endMillisEpoch = dateFilterRange.endDate

                sortedCollection.filter { it.timeCreated in startMillisEpoch..endMillisEpoch }
            } else {
                sortedCollection
            }

            dateFilteredCollection
        }
    }
}