package com.deepvisiontech.thecomicinator3000.core.domain.repository

import com.deepvisiontech.thecomicinator3000.core.domain.model.EvilResponse
import kotlinx.coroutines.flow.Flow

interface StorageAccessRepository {
    val storageUri: Flow<String?>

    suspend fun setStorageUri(uri: String?): EvilResponse<Unit>
}