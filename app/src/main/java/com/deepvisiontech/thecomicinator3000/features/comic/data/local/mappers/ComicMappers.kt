package com.deepvisiontech.thecomicinator3000.features.comic.data.local.mappers

import com.deepvisiontech.thecomicinator3000.features.comic.data.local.entity.ComicEntity
import com.deepvisiontech.thecomicinator3000.features.comic.domain.model.Comic

fun Comic.toEntity(): ComicEntity {
    return ComicEntity(
        id = this.id,
        displayName = this.displayName,
        lastOpened = this.lastOpened,
        fileUri = this.fileUri,
        coverImageUri = this.coverImageUri,
        fileSize = this.fileSize
    )
}

fun ComicEntity.toComic(): Comic {
    return Comic(
        id = this.id,
        displayName = this.displayName,
        lastOpened = this.lastOpened,
        fileUri = this.fileUri,
        coverImageUri = this.coverImageUri,
        fileSize = this.fileSize
    )
}