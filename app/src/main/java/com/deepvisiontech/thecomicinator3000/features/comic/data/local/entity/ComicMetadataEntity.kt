package com.deepvisiontech.thecomicinator3000.features.comic.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "metadata",
    foreignKeys = [
        ForeignKey(
            entity = ComicEntity::class,
            parentColumns = ["id"],
            childColumns = ["comicId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ComicMetadataEntity(
    @PrimaryKey
    val comicId: String,
    val title: String?,
    val series: String?,
    val number: String?,
    val genre: String?,
    val year: String?
)
