package com.deepvisiontech.thecomicinator3000.features.comic.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("collection")
data class ComicCollectionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val displayName: String,
    val dateCreated: Long
)
