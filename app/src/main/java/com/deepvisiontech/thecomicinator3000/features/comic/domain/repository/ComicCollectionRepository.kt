package com.deepvisiontech.thecomicinator3000.features.comic.domain.repository

import com.deepvisiontech.thecomicinator3000.core.domain.model.EvilResponse
import com.deepvisiontech.thecomicinator3000.features.comic.domain.model.ComicCollection
import kotlinx.coroutines.flow.Flow

interface ComicCollectionRepository {

    fun getAllComicCollection(): Flow<List<ComicCollection>>

    suspend fun insertCollection(collection: ComicCollection): EvilResponse<Unit>

    suspend fun deleteCollections(collections: List<ComicCollection>): EvilResponse<Unit>
}