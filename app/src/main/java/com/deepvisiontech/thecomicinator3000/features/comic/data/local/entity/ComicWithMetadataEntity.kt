package com.deepvisiontech.thecomicinator3000.features.comic.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class ComicWithMetadataEntity(
    @Embedded
    val comic: ComicEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "comicId"
    )
    val metadata: ComicMetadataEntity?
)
