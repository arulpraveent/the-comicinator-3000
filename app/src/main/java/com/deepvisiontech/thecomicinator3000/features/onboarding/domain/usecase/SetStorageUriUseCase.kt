package com.deepvisiontech.thecomicinator3000.features.onboarding.domain.usecase

import android.net.Uri
import com.deepvisiontech.thecomicinator3000.core.domain.model.EvilResponse
import com.deepvisiontech.thecomicinator3000.core.domain.repository.StorageAccessRepository
import javax.inject.Inject

class SetStorageUriUseCase @Inject constructor(
    private val storageAccessRepository: StorageAccessRepository
) {
    suspend operator fun invoke(uri: String?): EvilResponse<Unit> {
        return storageAccessRepository.setStorageUri(uri)
    }
}