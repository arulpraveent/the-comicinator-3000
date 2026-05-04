package com.deepvisiontech.thecomicinator3000.features.comic.data.local.mappers

import com.deepvisiontech.thecomicinator3000.features.comic.data.local.entity.ComicCollectionEntity
import com.deepvisiontech.thecomicinator3000.features.comic.domain.model.ComicCollection

fun ComicCollection.toEntity(): ComicCollectionEntity {
    return ComicCollectionEntity(
        this.id,
        this.displayName,
        this.timeCreated
    )
}

fun ComicCollectionEntity.toComicCollection(): ComicCollection {
    return ComicCollection(
        this.id,
        this.displayName,
        this.dateCreated
    )
}