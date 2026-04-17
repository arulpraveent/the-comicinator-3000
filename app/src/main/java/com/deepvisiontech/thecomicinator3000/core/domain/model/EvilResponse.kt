package com.deepvisiontech.thecomicinator3000.core.domain.model

sealed interface EvilResponse<out T> {

    data class Success<T>(
        val data: T
    ) : EvilResponse<T>

    data class Failure(
        val error: AppError
    ) : EvilResponse<Nothing>
}