package com.sotti.product.musictrack.dto

data class QuestionResponse(
    val questionToken: String,
    val previewSeconds: Int,
    val youtube: YoutubeInfo,
    val choices: List<ChoiceItem>,
)

data class YoutubeInfo(
    val videoId: String,
    val startSeconds: Int,
)

data class ChoiceItem(
    val trackId: String,
    val thumbnailUrl: String,
)
