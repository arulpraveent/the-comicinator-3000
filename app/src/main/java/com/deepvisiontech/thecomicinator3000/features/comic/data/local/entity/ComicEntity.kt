package com.deepvisiontech.thecomicinator3000.features.comic.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    "comics",
    foreignKeys = [
        ForeignKey(
            entity = ComicCollectionEntity::class,
            parentColumns = ["id"],
            childColumns = ["collectionId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices =[
        Index(value = ["collectionId"])
    ]
)
data class ComicEntity(
    @PrimaryKey
    val id: String,
    val collectionId: Long? = null,
    @ColumnInfo("display_name")
    val displayName: String,
    @ColumnInfo("last_opened")
    val lastOpened: Long,
    @ColumnInfo("file_uri")
    val fileUri: String,
    @ColumnInfo("cover_image_uri")
    val coverImageUri: String? = null,
    @ColumnInfo("file_size")
    val fileSize: Long
)
