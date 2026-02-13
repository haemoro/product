package com.sotti.product.musictrack.dto

import com.sotti.product.musictrack.domain.MusicTrack
import com.sotti.product.musictrack.domain.TrackStatus
import java.time.LocalDateTime

data class MusicTrackResponse(
    val id: String,
    val youtubeVideoId: String,
    val startSeconds: Int,
    val thumbnailUrl: String,
    val status: TrackStatus,
    val title: String?,
    val imageUrl: String?,
    val category: String?,
    val difficulty: String?,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
) {
    companion object {
        fun from(track: MusicTrack): MusicTrackResponse =
            MusicTrackResponse(
                id = track.id!!,
                youtubeVideoId = track.youtubeVideoId,
                startSeconds = track.startSeconds,
                thumbnailUrl = track.thumbnailUrl,
                status = track.status,
                title = track.title,
                imageUrl = track.imageUrl,
                category = track.category,
                difficulty = track.difficulty,
                createdAt = track.createdAt,
                updatedAt = track.updatedAt,
            )
    }
}
