package com.sotti.product.dto

import jakarta.validation.constraints.NotNull

data class UpdateAllowedCategoriesRequest(
    @field:NotNull(message = "카테고리 ID 목록은 필수입니다")
    val categoryIds: List<String>,
)
