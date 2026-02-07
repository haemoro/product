package com.sotti.product.dto

data class UpdateQuizCategoryRequest(
    val name: String? = null,
    val imageUrl: String? = null,
    val visible: Boolean? = null,
    val displayOrder: Int? = null,
)
