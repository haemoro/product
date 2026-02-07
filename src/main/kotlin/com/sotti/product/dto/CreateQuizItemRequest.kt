package com.sotti.product.dto

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty

data class CreateQuizItemRequest(
    @field:NotBlank(message = "카테고리 ID는 필수입니다")
    val categoryId: String,
    @field:NotBlank(message = "이미지 URL은 필수입니다")
    val imageUrl: String,
    @field:NotBlank(message = "아이템 이름은 필수입니다")
    val name: String,
    @field:NotBlank(message = "이름 음성 URL은 필수입니다")
    val nameVoice: String,
    @field:NotEmpty(message = "질문 목록은 최소 1개 이상 필요합니다")
    @field:Valid
    val questions: List<CreateQuestionRequest>,
)

data class CreateQuestionRequest(
    @field:NotBlank(message = "질문 텍스트는 필수입니다")
    val question: String,
    @field:NotBlank(message = "질문 음성 URL은 필수입니다")
    val questionVoice: String,
)
