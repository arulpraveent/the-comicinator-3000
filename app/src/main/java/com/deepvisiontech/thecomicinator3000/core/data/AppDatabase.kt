package com.deepvisiontech.thecomicinator3000.core.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.deepvisiontech.thecomicinator3000.features.comic.data.local.dao.ComicCollectionDao
import com.deepvisiontech.thecomicinator3000.features.comic.data.local.dao.ComicDao
import com.deepvisiontech.thecomicinator3000.features.comic.data.local.entity.ComicCollectionEntity
import com.deepvisiontech.thecomicinator3000.features.comic.data.local.entity.ComicEntity
import com.deepvisiontech.thecomicinator3000.features.comic.data.local.entity.ComicMetadataEntity

@Database(entities = [
    ComicEntity::class,
    ComicMetadataEntity::class,
    ComicCollectionEntity::class
], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun comicDao(): ComicDao
    abstract fun comicCollectionDao(): ComicCollectionDao

    companion object {
        const val DATABASE_NAME = "ComicinatorDb.db"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = buildDatabaseWithRecovery(context)
                INSTANCE = instance
                instance
            }
        }

        private fun buildDatabaseWithRecovery(context: Context): AppDatabase {
            try {
                return Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    .build()
            } catch (e: Exception) {
                throw IllegalStateException(
                    "FATAL ERROR: Failed to build or migrate the database. Crashing to prevent data loss.",
                    e
                )
            }
        }
    }
}