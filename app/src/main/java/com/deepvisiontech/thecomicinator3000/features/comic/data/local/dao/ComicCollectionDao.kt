package com.deepvisiontech.thecomicinator3000.features.comic.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.deepvisiontech.thecomicinator3000.features.comic.data.local.entity.ComicCollectionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ComicCollectionDao {

    @Query("SELECT * FROM collection ORDER BY dateCreated DESC")
    fun getAllCollectionsFlow(): Flow<List<ComicCollectionEntity>>

    @Insert
    suspend fun insertCollection(collection: ComicCollectionEntity)

    @Delete
    suspend fun deleteCollections(collections: List<ComicCollectionEntity>)
}