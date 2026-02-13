package com.sotti.product.musictrack.domain

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "music_track")
@CompoundIndex(name = "idx_status", def = "{'status': 1}")
data class MusicTrack(
    @Id
    val id: String? = null,
    @Indexed(unique = true)
    val youtubeVideoId: String,
    val startSeconds: Int,
    val thumbnailUrl: String,
    val status: TrackStatus = TrackStatus.ACTIVE,
    val title: String,
    val imageUrl: String? = null,
    val category: String? = null,
    val difficulty: String? = null,
    @CreatedDate
    val createdAt: LocalDateTime? = null,
    @LastModifiedDate
    val updatedAt: LocalDateTime? = null,
)
