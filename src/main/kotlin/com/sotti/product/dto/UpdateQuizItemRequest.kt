package com.sotti.product.dto

import jakarta.validation.Valid

data class UpdateQuizItemRequest(
    val categoryId: String? = null,
    val imageUrl: String? = null,
    val name: String? = null,
    val nameVoice: String? = null,
    @field:Valid
    val questions: List<CreateQuestionRequest>? = null,
)
