package com.deepvisiontech.thecomicinator3000.features.di

import android.content.Context
import com.deepvisiontech.thecomicinator3000.core.data.AppDatabase
import com.deepvisiontech.thecomicinator3000.features.comic.data.local.dao.ComicCollectionDao
import com.deepvisiontech.thecomicinator3000.features.comic.data.local.dao.ComicDao
import com.deepvisiontech.thecomicinator3000.features.comic.data.local.services.ComicScannerService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ComicModule {

    @Provides
    @Singleton
    fun provideComicDao(db: AppDatabase): ComicDao {
        return db.comicDao()
    }

    @Provides
    @Singleton
    fun provideComicCollectionDao(db: AppDatabase): ComicCollectionDao {
        return db.comicCollectionDao()
    }
}
