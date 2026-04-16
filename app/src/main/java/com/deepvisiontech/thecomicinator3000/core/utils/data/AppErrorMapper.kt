package com.deepvisiontech.thecomicinator3000.core.utils.data

import com.deepvisiontech.thecomicinator3000.features.common.domain.model.AppError

fun Throwable.toAppError(): AppError {
    return when (this) {
        is java.io.IOException -> AppError.Network
        is IllegalArgumentException -> AppError.Validation
        is android.database.SQLException -> AppError.Database
        else -> AppError.Unknown(this)
    }
}