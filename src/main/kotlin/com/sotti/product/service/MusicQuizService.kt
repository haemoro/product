package com.sotti.product.service

import com.sotti.product.domain.Category
import com.sotti.product.domain.MusicQuiz
import com.sotti.product.dto.CheckAnswerRequest
import com.sotti.product.dto.CheckAnswerResponse
import com.sotti.product.dto.CreateMusicQuizRequest
import com.sotti.product.dto.MusicQuizGameResponse
import com.sotti.product.dto.MusicQuizResponse
import com.sotti.product.dto.UpdateMusicQuizRequest
import com.sotti.product.repository.MusicQuizRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class MusicQuizService(
    private val musicQuizRepository: MusicQuizRepository,
    private val mongoTemplate: MongoTemplate,
) {
    // 퀴즈 생성
    fun createQuiz(request: CreateMusicQuizRequest): MusicQuizResponse {
        val musicQuiz =
            MusicQuiz(
                musicUrl = request.musicUrl,
                answer = request.answer,
                imageUrl = request.imageUrl,
                title = request.title,
                category = request.category,
            )

        val saved = musicQuizRepository.save(musicQuiz)
        return MusicQuizResponse.from(saved)
    }

    // 퀴즈 단건 조회
    @Transactional(readOnly = true)
    fun getQuizById(id: String): MusicQuizResponse {
        val musicQuiz =
            musicQuizRepository.findByIdOrNull(id)
                ?: throw NoSuchElementException("퀴즈를 찾을 수 없습니다. ID: $id")
        return MusicQuizResponse.from(musicQuiz)
    }

    // 게임용 퀴즈 조회 (정답 숨김)
    @Transactional(readOnly = true)
    fun getQuizForGame(id: String): MusicQuizGameResponse {
        val musicQuiz =
            musicQuizRepository.findByIdOrNull(id)
                ?: throw NoSuchElementException("퀴즈를 찾을 수 없습니다. ID: $id")

        return MusicQuizGameResponse.from(musicQuiz)
    }

    // 전체 퀴즈 목록 조회 (페이징)
    @Transactional(readOnly = true)
    fun getAllQuizzes(pageable: Pageable): Page<MusicQuizResponse> =
        musicQuizRepository
            .findAll(pageable)
            .map { MusicQuizResponse.from(it) }

    // 카테고리별 퀴즈 조회
    @Transactional(readOnly = true)
    fun getQuizzesByCategory(category: Category): List<MusicQuizResponse> =
        musicQuizRepository
            .findByCategory(category)
            .map { MusicQuizResponse.from(it) }

    // 랜덤 퀴즈 조회
    @Transactional(readOnly = true)
    fun getRandomQuiz(category: Category? = null): MusicQuizGameResponse {
        val operations =
            mutableListOf<org.springframework.data.mongodb.core.aggregation.AggregationOperation>()

        if (category != null) {
            operations.add(Aggregation.match(Criteria.where("category").`is`(category)))
        }
        operations.add(Aggregation.sample(1))

        val aggregation = Aggregation.newAggregation(operations)
        val result = mongoTemplate.aggregate(aggregation, "music_quiz", MusicQuiz::class.java)

        val randomQuiz =
            result.mappedResults.firstOrNull()
                ?: throw NoSuchElementException("조건에 맞는 퀴즈가 없습니다.")

        return MusicQuizGameResponse.from(randomQuiz)
    }

    // 퀴즈 수정
    fun updateQuiz(
        id: String,
        request: UpdateMusicQuizRequest,
    ): MusicQuizResponse {
        val musicQuiz =
            musicQuizRepository.findByIdOrNull(id)
                ?: throw NoSuchElementException("퀴즈를 찾을 수 없습니다. ID: $id")

        val updated =
            musicQuiz.copy(
                musicUrl = request.musicUrl ?: musicQuiz.musicUrl,
                answer = request.answer ?: musicQuiz.answer,
                imageUrl = request.imageUrl ?: musicQuiz.imageUrl,
                title = request.title ?: musicQuiz.title,
                category = request.category ?: musicQuiz.category,
            )

        val saved = musicQuizRepository.save(updated)
        return MusicQuizResponse.from(saved)
    }

    // 퀴즈 삭제
    fun deleteQuiz(id: String) {
        if (!musicQuizRepository.existsById(id)) {
            throw NoSuchElementException("퀴즈를 찾을 수 없습니다. ID: $id")
        }
        musicQuizRepository.deleteById(id)
    }

    // 정답 체크
    fun checkAnswer(request: CheckAnswerRequest): CheckAnswerResponse {
        val musicQuiz =
            musicQuizRepository.findByIdOrNull(request.quizId)
                ?: throw NoSuchElementException("퀴즈를 찾을 수 없습니다. ID: ${request.quizId}")

        val isCorrect = musicQuiz.answer.equals(request.userAnswer.trim(), ignoreCase = true)

        return CheckAnswerResponse(
            isCorrect = isCorrect,
            correctAnswer = musicQuiz.answer,
            message = if (isCorrect) "정답입니다! 🎉" else "아쉬워요, 다시 한번 생각해보세요! 💪",
        )
    }
}
