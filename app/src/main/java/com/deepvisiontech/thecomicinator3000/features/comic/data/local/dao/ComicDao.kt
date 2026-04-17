package com.deepvisiontech.thecomicinator3000.features.comic.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.deepvisiontech.thecomicinator3000.features.comic.data.local.entity.ComicEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ComicDao {

    @Query("SELECT * FROM comics ORDER BY last_opened DESC")
    fun getAllComics(): Flow<List<ComicEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertComic(comic: ComicEntity)

    @Query("UPDATE comics SET last_opened = :timestamp WHERE id = :comicId")
    suspend fun updateLastOpened(comicId: String, timestamp: Long)

    @Query("UPDATE comics SET cover_image_uri = :coverUri WHERE id = :comicId")
    suspend fun updateCover(comicId: String, coverUri: String)

    @Delete
    suspend fun deleteComic(comic: ComicEntity)

    @Query("DELETE FROM comics WHERE id NOT IN (:validIds)")
    suspend fun deleteMissingComics(validIds: List<String>)
}