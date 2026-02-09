package com.sotti.product.musictrack.dto

import jakarta.validation.constraints.NotBlank

data class CheckTrackAnswerRequest(
    @field:NotBlank(message = "questionToken은 필수입니다")
    val questionToken: String,
    @field:NotBlank(message = "selectedTrackId는 필수입니다")
    val selectedTrackId: String,
)
