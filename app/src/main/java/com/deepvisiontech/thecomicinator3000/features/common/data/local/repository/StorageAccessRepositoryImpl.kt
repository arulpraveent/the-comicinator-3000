package com.deepvisiontech.thecomicinator3000.features.common.data.local.repository

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.deepvisiontech.thecomicinator3000.features.common.data.storageAccessDataStore
import com.deepvisiontech.thecomicinator3000.features.common.domain.model.EvilResponse
import com.deepvisiontech.thecomicinator3000.features.common.domain.repository.StorageAccessRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class StorageAccessRepositoryImpl @Inject constructor(
    @ApplicationContext val context: Context
) : StorageAccessRepository {

    private val dataStore: DataStore<Preferences> = context.storageAccessDataStore

    private object PreferenceKeys {
        val STORAGE_URI = stringPreferencesKey("datastore_uri")
        val STORAGE_ACCESS = booleanPreferencesKey("storage_access")
    }

    override val storageUri: Flow<Uri?> = dataStore.data
        .catch { e ->
            emit(emptyPreferences())
        }.map { preferences ->
            runCatching {
                preferences[PreferenceKeys.STORAGE_URI]?.toUri()
            }.getOrNull()
        }

    override val isStorageAccessGranted: Flow<Boolean> = dataStore.data
        .catch { e ->
            emit(emptyPreferences())
        }.map { preferences ->
            preferences[PreferenceKeys.STORAGE_ACCESS] ?: false
        }

    override suspend fun setStorageUri(): EvilResponse<Nothing> {
        TODO("Not yet implemented")
    }

    override suspend fun setStorageAccessState(): EvilResponse<Nothing> {
        TODO("Not yet implemented")
    }


}