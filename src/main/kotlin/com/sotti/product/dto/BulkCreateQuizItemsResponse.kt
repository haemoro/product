package com.sotti.product.dto

data class BulkCreateQuizItemsResponse(
    val categoryId: String,
    val categoryName: String,
    val createdCount: Int,
    val items: List<QuizItemResponse>,
)
