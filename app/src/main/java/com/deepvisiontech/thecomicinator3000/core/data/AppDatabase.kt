package com.deepvisiontech.thecomicinator3000.core.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.deepvisiontech.thecomicinator3000.features.comic.data.local.dao.ComicDao
import com.deepvisiontech.thecomicinator3000.features.comic.data.local.entity.ComicEntity

@Database(entities = [ComicEntity::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun comicDao(): ComicDao
}