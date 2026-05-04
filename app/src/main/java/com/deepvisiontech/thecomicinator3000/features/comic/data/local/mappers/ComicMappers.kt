package com.deepvisiontech.thecomicinator3000.features.comic.data.local.mappers

import com.deepvisiontech.thecomicinator3000.features.comic.data.local.entity.ComicEntity
import com.deepvisiontech.thecomicinator3000.features.comic.data.local.entity.ComicMetadataEntity
import com.deepvisiontech.thecomicinator3000.features.comic.data.local.entity.ComicWithMetadataEntity
import com.deepvisiontech.thecomicinator3000.features.comic.domain.model.Comic
import com.deepvisiontech.thecomicinator3000.features.comic.domain.model.ComicMetadata

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

fun ComicMetadataEntity.toComicMetadata(): ComicMetadata {
    return ComicMetadata(
        title = this.title,
        series = this.series,
        number = this.number,
        genre = this.genre,
        year = this.year
    )
}

fun ComicWithMetadataEntity.toComic(): Comic {
    return Comic(
        id = this.comic.id,
        collectionId = this.comic.collectionId,
        displayName = this.comic.displayName,
        lastOpened = this.comic.lastOpened,
        fileUri = this.comic.fileUri,
        coverImageUri = this.comic.coverImageUri,
        fileSize = this.comic.fileSize,
        metadata = this.metadata?.toComicMetadata()
    )
}