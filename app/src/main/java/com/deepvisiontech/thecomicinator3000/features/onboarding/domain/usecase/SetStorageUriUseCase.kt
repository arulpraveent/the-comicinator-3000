package com.deepvisiontech.thecomicinator3000.features.onboarding.domain.usecase

import android.net.Uri
import com.deepvisiontech.thecomicinator3000.features.common.domain.model.EvilResponse
import com.deepvisiontech.thecomicinator3000.features.common.domain.repository.StorageAccessRepository
import javax.inject.Inject

class SetStorageUriUseCase @Inject constructor(
    private val storageAccessRepository: StorageAccessRepository
) {
    suspend operator fun invoke(uri: Uri): EvilResponse<Unit> {
        return storageAccessRepository.setStorageUri(uri)
    }
}