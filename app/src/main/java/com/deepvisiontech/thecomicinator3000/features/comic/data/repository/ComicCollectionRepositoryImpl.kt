package com.deepvisiontech.thecomicinator3000.features.comic.data.repository

import com.deepvisiontech.thecomicinator3000.core.data.utils.safeEvilResponseCall
import com.deepvisiontech.thecomicinator3000.core.domain.model.EvilResponse
import com.deepvisiontech.thecomicinator3000.features.comic.data.local.dao.ComicCollectionDao
import com.deepvisiontech.thecomicinator3000.features.comic.data.local.mappers.toComicCollection
import com.deepvisiontech.thecomicinator3000.features.comic.data.local.mappers.toEntity
import com.deepvisiontech.thecomicinator3000.features.comic.domain.model.ComicCollection
import com.deepvisiontech.thecomicinator3000.features.comic.domain.repository.ComicCollectionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ComicCollectionRepositoryImpl @Inject constructor(
    private val comicCollectionDao: ComicCollectionDao
): ComicCollectionRepository {
    override fun getAllComicCollection(): Flow<List<ComicCollection>> {
        return comicCollectionDao.getAllCollectionsFlow().map { entities -> entities.map { it.toComicCollection() } }
    }

    override fun getComicCollection(id: Long?): Flow<ComicCollection?> {
        if (id == null) {
            return flowOf(null)
        }
        return comicCollectionDao.getCollectionFlow(id).map { entity -> entity.toComicCollection() }
    }

    override suspend fun insertCollection(collection: ComicCollection): EvilResponse<Unit> {
        return safeEvilResponseCall(TAG) {
            comicCollectionDao.insertCollection(collection.toEntity())
        }
    }

    override suspend fun deleteCollections(collections: List<ComicCollection>): EvilResponse<Unit> {
        return safeEvilResponseCall(TAG) {
            comicCollectionDao.deleteCollections(collections.map { it.toEntity() })
        }
    }

    private companion object {
        private const val TAG = "ComicCollectionRepositoryImpl"
    }
}