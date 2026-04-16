package com.deepvisiontech.thecomicinator3000.core.utils.data

import android.util.Log
import com.deepvisiontech.thecomicinator3000.features.common.domain.model.EvilResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException

suspend fun <T> safeEvilResponseCall(
    tag: String,
    block: suspend () -> T
): EvilResponse<T> = withContext(Dispatchers.IO) {
    try {
        EvilResponse.Success(block())
    } catch (e: Throwable) {
        if (e is CancellationException) throw e

        Log.e(tag, "Error occurred", e)

        EvilResponse.Failure(e.toAppError())
    }
}