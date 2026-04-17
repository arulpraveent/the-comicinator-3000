package com.deepvisiontech.thecomicinator3000.core.di

import android.content.Context
import com.deepvisiontech.thecomicinator3000.core.data.local.repository.StorageAccessRepositoryImpl
import com.deepvisiontech.thecomicinator3000.core.domain.repository.StorageAccessRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideStorageAccessRepository(@ApplicationContext context: Context): StorageAccessRepository {
        return StorageAccessRepositoryImpl(context)
    }
}