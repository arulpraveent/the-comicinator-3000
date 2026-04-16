package com.deepvisiontech.thecomicinator3000.features.common.data.local.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.deepvisiontech.thecomicinator3000.core.utils.data.safeEvilResponseCall
import com.deepvisiontech.thecomicinator3000.features.common.data.storageAccessDataStore
import com.deepvisiontech.thecomicinator3000.features.common.domain.model.EvilResponse
import com.deepvisiontech.thecomicinator3000.features.common.domain.repository.StorageAccessRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile

class StorageAccessRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : StorageAccessRepository {

    private val dataStore: DataStore<Preferences> = context.storageAccessDataStore

    private object PreferenceKeys {
        val STORAGE_URI = stringPreferencesKey("datastore_uri")
    }

    override val storageUri: Flow<String?> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { preferences ->
            val savedUri = preferences[PreferenceKeys.STORAGE_URI]

            when {
                savedUri.isNullOrBlank() -> null
                isStorageUriValid(savedUri) -> savedUri
                else -> {
                    Log.w(TAG, "Stored URI is no longer valid. Emitting null.")
                    null
                }
            }
        }
        .distinctUntilChanged()

    override suspend fun setStorageUri(uri: String?): EvilResponse<Unit> {
        return safeEvilResponseCall(TAG) {
            dataStore.edit { preferences ->
                if (uri.isNullOrBlank()) {
                    preferences.remove(PreferenceKeys.STORAGE_URI)
                } else {
                    preferences[PreferenceKeys.STORAGE_URI] = uri
                }
            }
        }
    }

    private fun isStorageUriValid(uriString: String): Boolean {
        val uri = uriString.toUri()

        val hasPersisted = context.contentResolver.persistedUriPermissions.any {
            it.uri == uri && it.isReadPermission
        }
        if (!hasPersisted) return false

        return try {
            val documentFile = DocumentFile.fromTreeUri(context, uri)
            documentFile?.exists() == true && documentFile.isDirectory
        } catch (e: Exception) {
            false
        }
    }

    private companion object {
        private const val TAG = "StorageAccessRepositoryImpl"
    }
}