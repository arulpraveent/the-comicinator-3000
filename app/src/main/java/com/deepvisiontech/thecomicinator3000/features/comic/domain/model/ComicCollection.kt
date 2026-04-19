package com.deepvisiontech.thecomicinator3000.features.comic.domain.model

data class ComicCollection(
    val id: Long,
    val displayName: String,
    val timeCreated: Long,
    val isSelected: Boolean = false
)
