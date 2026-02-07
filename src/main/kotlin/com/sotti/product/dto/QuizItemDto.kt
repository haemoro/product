package com.sotti.product.dto

import com.sotti.product.domain.Question
import com.sotti.product.domain.QuizItem

data class QuizItemResponse(
    val id: String,
    val categoryId: String,
    val imageUrl: String,
    val name: String,
    val nameVoice: String,
    val questions: List<QuestionResponse>,
) {
    companion object {
        fun from(quizItem: QuizItem): QuizItemResponse =
            QuizItemResponse(
                id = quizItem.id!!,
                categoryId = quizItem.categoryId,
                imageUrl = quizItem.imageUrl,
                name = quizItem.name,
                nameVoice = quizItem.nameVoice,
                questions = quizItem.questions.map { QuestionResponse.from(it) },
            )
    }
}

data class QuestionResponse(
    val question: String,
    val questionVoice: String,
) {
    companion object {
        fun from(question: Question): QuestionResponse =
            QuestionResponse(
                question = question.question,
                questionVoice = question.questionVoice,
            )
    }
}
