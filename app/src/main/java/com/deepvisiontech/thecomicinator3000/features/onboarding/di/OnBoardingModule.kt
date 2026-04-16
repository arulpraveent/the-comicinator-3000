package com.deepvisiontech.thecomicinator3000.features.onboarding.di

import android.content.Context
import com.deepvisiontech.thecomicinator3000.features.common.data.local.repository.StorageAccessRepositoryImpl
import com.deepvisiontech.thecomicinator3000.features.common.domain.repository.StorageAccessRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OnBoardingModule {

    @Provides
    @Singleton
    fun provideStorageAccessRepository(@ApplicationContext context: Context): StorageAccessRepository {
        return StorageAccessRepositoryImpl(context)
    }
}