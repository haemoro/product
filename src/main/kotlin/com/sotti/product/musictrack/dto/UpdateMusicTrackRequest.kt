package com.sotti.product.musictrack.dto

import com.sotti.product.musictrack.domain.TrackStatus

data class UpdateMusicTrackRequest(
    val youtubeVideoId: String? = null,
    val startSeconds: Int? = null,
    val thumbnailUrl: String? = null,
    val status: TrackStatus? = null,
    val title: String? = null,
    val imageUrl: String? = null,
    val category: String? = null,
    val difficulty: String? = null,
)
