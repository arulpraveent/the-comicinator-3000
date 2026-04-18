package com.deepvisiontech.thecomicinator3000.features.di

import com.deepvisiontech.thecomicinator3000.features.comic.data.local.repository.ComicCollectionRepositoryImpl
import com.deepvisiontech.thecomicinator3000.features.comic.data.local.repository.ComicRepositoryImpl
import com.deepvisiontech.thecomicinator3000.features.comic.domain.repository.ComicCollectionRepository
import com.deepvisiontech.thecomicinator3000.features.comic.domain.repository.ComicRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ComicRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindComicRepository(
        impl: ComicRepositoryImpl
    ): ComicRepository

    @Binds
    @Singleton
    abstract fun bindComicCollectionRepository(
        impl: ComicCollectionRepositoryImpl
    ): ComicCollectionRepository
}