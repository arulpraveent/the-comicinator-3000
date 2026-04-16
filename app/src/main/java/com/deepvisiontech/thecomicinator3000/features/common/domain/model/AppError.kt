package com.deepvisiontech.thecomicinator3000.features.common.domain.model

sealed interface AppError {

    val cause: Throwable?

    data object Network : AppError {
        override val cause: Throwable? = null
    }

    data object Database : AppError {
        override val cause: Throwable? = null
    }

    data object Validation : AppError {
        override val cause: Throwable? = null
    }

    data class Unknown(
        override val cause: Throwable?
    ) : AppError
}