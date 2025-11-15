package com.sotti.product.dto

import com.sotti.product.domain.Category
import com.sotti.product.domain.MusicQuiz
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

// 생성 요청 DTO
data class CreateMusicQuizRequest(
    @field:NotBlank(message = "음원 URL은 필수입니다")
    val musicUrl: String,
    @field:NotBlank(message = "정답은 필수입니다")
    val answer: String,
    val imageUrl: String? = null,
    @field:NotBlank(message = "제목은 필수입니다")
    val title: String,
    @field:NotNull(message = "카테고리는 필수입니다")
    val category: Category,
)

// 수정 요청 DTO
data class UpdateMusicQuizRequest(
    val musicUrl: String? = null,
    val answer: String? = null,
    val imageUrl: String? = null,
    val title: String? = null,
    val category: Category? = null,
)

// 응답 DTO
data class MusicQuizResponse(
    val id: String,
    val musicUrl: String,
    val answer: String,
    val imageUrl: String?,
    val title: String,
    val category: Category,
) {
    companion object {
        fun from(musicQuiz: MusicQuiz): MusicQuizResponse =
            MusicQuizResponse(
                id = musicQuiz.id!!,
                musicUrl = musicQuiz.musicUrl,
                answer = musicQuiz.answer,
                imageUrl = musicQuiz.imageUrl,
                title = musicQuiz.title,
                category = musicQuiz.category,
            )
    }
}

// 게임 플레이용 응답 DTO (정답 숨김)
data class MusicQuizGameResponse(
    val id: String,
    val musicUrl: String,
    val imageUrl: String?,
    val title: String,
    val category: Category,
) {
    companion object {
        fun from(musicQuiz: MusicQuiz): MusicQuizGameResponse =
            MusicQuizGameResponse(
                id = musicQuiz.id!!,
                musicUrl = musicQuiz.musicUrl,
                imageUrl = musicQuiz.imageUrl,
                title = musicQuiz.title,
                category = musicQuiz.category,
            )
    }
}

// 정답 체크 요청 DTO
data class CheckAnswerRequest(
    @field:NotBlank(message = "퀴즈 ID는 필수입니다")
    val quizId: String,
    @field:NotBlank(message = "답안은 필수입니다")
    val userAnswer: String,
)

// 정답 체크 응답 DTO
data class CheckAnswerResponse(
    val isCorrect: Boolean,
    val correctAnswer: String,
    val message: String,
)
