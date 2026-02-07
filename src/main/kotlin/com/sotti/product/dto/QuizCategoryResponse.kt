package com.sotti.product.dto

import com.sotti.product.domain.QuizCategory

data class QuizCategoryResponse(
    val id: String,
    val name: String,
    val imageUrl: String?,
    val visible: Boolean,
    val displayOrder: Int,
) {
    companion object {
        fun from(quizCategory: QuizCategory): QuizCategoryResponse =
            QuizCategoryResponse(
                id = quizCategory.id!!,
                name = quizCategory.name,
                imageUrl = quizCategory.imageUrl,
                visible = quizCategory.visible,
                displayOrder = quizCategory.displayOrder,
            )
    }
}
