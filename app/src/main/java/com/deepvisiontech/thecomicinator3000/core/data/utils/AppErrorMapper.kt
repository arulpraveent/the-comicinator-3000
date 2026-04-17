package com.deepvisiontech.thecomicinator3000.core.data.utils

import android.database.SQLException
import com.deepvisiontech.thecomicinator3000.core.domain.model.AppError
import java.io.IOException

fun Throwable.toAppError(): AppError {
    return when (this) {
        is IOException -> AppError.Network
        is IllegalArgumentException -> AppError.Validation
        is SQLException -> AppError.Database
        else -> AppError.Unknown(this)
    }
}