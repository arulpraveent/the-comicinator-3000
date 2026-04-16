package com.deepvisiontech.thecomicinator3000.features.common.domain.repository

import android.net.Uri
import com.deepvisiontech.thecomicinator3000.features.common.domain.model.EvilResponse
import kotlinx.coroutines.flow.Flow

interface StorageAccessRepository {
    val storageUri: Flow<Uri?>

    val isStorageAccessGranted: Flow<Boolean>

    suspend fun setStorageUri(uri: Uri): EvilResponse<Unit>

    suspend fun setStorageAccessState(state: Boolean): EvilResponse<Unit>
}