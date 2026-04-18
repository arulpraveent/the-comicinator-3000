package com.deepvisiontech.thecomicinator3000.features.comic.domain.repository

import com.deepvisiontech.thecomicinator3000.core.domain.model.EvilResponse
import com.deepvisiontech.thecomicinator3000.features.comic.domain.model.Comic
import kotlinx.coroutines.flow.Flow

interface ComicRepository {

    suspend fun scanAndSyncComics(uriString: String): EvilResponse<Unit>

    fun getAllComicsFlow(): Flow<List<Comic>>

    fun getAllComicsOfCollectionFlow(id: Long): Flow<List<Comic>>

    suspend fun addComicsToCollection(comicIds: List<String>, collectionId: Long): EvilResponse<Unit>

    suspend fun insertComics(comics: List<Comic>): EvilResponse<Unit>

    suspend fun deleteComics(comics: List<Comic>): EvilResponse<Unit>

    suspend fun updateLastOpened(comicId: String, timeStamp: Long): EvilResponse<Unit>
}