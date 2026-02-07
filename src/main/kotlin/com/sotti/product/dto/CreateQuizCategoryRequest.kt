package com.sotti.product.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class CreateQuizCategoryRequest(
    @field:NotBlank(message = "카테고리명은 필수입니다")
    val name: String,
    @field:NotNull(message = "노출 여부는 필수입니다")
    val visible: Boolean,
    val displayOrder: Int = 0,
)
