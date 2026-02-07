package com.sotti.product.dto

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty

data class BulkCreateQuizItemsRequest(
    @field:NotBlank(message = "카테고리 이름은 필수입니다")
    val categoryName: String,
    val categoryImageUrl: String? = null,
    @field:NotEmpty(message = "아이템 목록은 최소 1개 이상 필요합니다")
    @field:Valid
    val items: List<CreateQuizItemPayload>,
)

data class CreateQuizItemPayload(
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
