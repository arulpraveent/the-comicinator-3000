package com.deepvisiontech.thecomicinator3000.features.comic.data.local.helpers

import android.content.Context
import android.net.Uri

fun isUriAccessible(context: Context, uri: Uri): Boolean {
    return try {
        context.contentResolver.openInputStream(uri)?.use {
            true
        } ?: false
    } catch (e: Exception) {
        false
    }
}

fun isImageFile(name: String): Boolean {
    val lower = name.lowercase()
    return lower.endsWith(".jpg") || lower.endsWith(".jpeg") ||
            lower.endsWith(".png") || lower.endsWith(".webp")
}