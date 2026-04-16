package com.deepvisiontech.thecomicinator3000.features.onboarding.domain.usecase

import android.net.Uri
import com.deepvisiontech.thecomicinator3000.features.common.domain.repository.StorageAccessRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetStoragePermissionUriFlow @Inject constructor(
    private val storageAccessRepository: StorageAccessRepository
) {
    operator fun invoke(): Flow<Uri?> {
        return storageAccessRepository.storageUri
    }
}