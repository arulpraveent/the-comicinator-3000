package com.deepvisiontech.thecomicinator3000.features.comic.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.deepvisiontech.thecomicinator3000.features.comic.data.local.entity.ComicEntity
import com.deepvisiontech.thecomicinator3000.features.comic.data.local.entity.ComicWithMetadataEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ComicDao {

    @Transaction
    @Query("SELECT * FROM comics ORDER BY last_opened DESC")
    fun getAllComicsWithMetadataFlow(): Flow<List<ComicWithMetadataEntity>>

    @Query("SELECT * FROM comics WHERE cover_image_uri IS NULL")
    suspend fun getAllComicsWithoutCover(): List<ComicEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertComics(comics: List<ComicEntity>)

    @Query("UPDATE comics SET last_opened = :timeStamp WHERE id = :comicId")
    suspend fun updateLastOpened(comicId: String, timeStamp: Long)

    @Query("UPDATE comics SET cover_image_uri = :coverUri WHERE id = :comicId")
    suspend fun updateCover(comicId: String, coverUri: String)

    @Query("""
        UPDATE metadata 
        SET title = :title, series = :series, number = :number, genre = :genre, year = :year 
        WHERE comicId = :comicId
    """)
    suspend fun updateMetadata(
        comicId: String, title: String?, series: String?, number: String?, genre: String?, year: String?
    )

    @Delete
    suspend fun deleteComics(comics: List<ComicEntity>)

    @Query("DELETE FROM comics WHERE id NOT IN (:validIds)")
    suspend fun deleteMissingComics(validIds: List<String>)
}