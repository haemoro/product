package com.sotti.product.musictrack.dto

data class CheckTrackAnswerResponse(
    val isCorrect: Boolean,
    val correctTrackId: String,
)
