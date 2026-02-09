package com.sotti.product.musictrack.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.PositiveOrZero

data class CreateMusicTrackRequest(
    @field:NotBlank(message = "youtubeVideoId는 필수입니다")
    val youtubeVideoId: String,
    @field:PositiveOrZero(message = "startSeconds는 0 이상이어야 합니다")
    val startSeconds: Int,
    val title: String? = null,
    val category: String? = null,
    val difficulty: String? = null,
)
