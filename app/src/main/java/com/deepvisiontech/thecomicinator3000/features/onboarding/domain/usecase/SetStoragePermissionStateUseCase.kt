package com.deepvisiontech.thecomicinator3000.features.onboarding.domain.usecase

import com.deepvisiontech.thecomicinator3000.features.common.domain.model.EvilResponse
import com.deepvisiontech.thecomicinator3000.features.common.domain.repository.StorageAccessRepository
import javax.inject.Inject

class SetStoragePermissionStateUseCase @Inject constructor(
    private val storageAccessRepository: StorageAccessRepository
) {
    suspend operator fun invoke(state: Boolean): EvilResponse<Unit> {
        return storageAccessRepository.setStorageAccessState(state)
    }
}