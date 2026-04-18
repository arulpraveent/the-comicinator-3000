package com.deepvisiontech.thecomicinator3000.features.comic.domain.model

data class Comic(
    val id: String,
    val displayName: String,
    val lastOpened: Long,
    val fileUri: String,
    val coverImageUri: String? = null,
    val fileSize: Long,
    val isSelected: Boolean = false,
    val metadata: ComicMetadata?
)
