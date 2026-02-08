package com.sotti.product.service

import com.sotti.product.domain.Question
import com.sotti.product.domain.QuizItem
import com.sotti.product.dto.CreateQuizItemPayload
import com.sotti.product.dto.CreateQuizItemRequest
import com.sotti.product.dto.QuizItemResponse
import com.sotti.product.dto.UpdateQuizItemRequest
import com.sotti.product.repository.QuizItemRepository
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional(readOnly = true)
class QuizItemService(
    private val quizItemRepository: QuizItemRepository,
    private val mongoTemplate: MongoTemplate,
) {
    @Transactional
    fun createItem(request: CreateQuizItemRequest): QuizItemResponse {
        val item =
            QuizItem(
                categoryId = request.categoryId,
                imageUrl = request.imageUrl,
                name = request.name,
                nameVoice = request.nameVoice,
                questions =
                    request.questions.map {
                        Question(
                            question = it.question,
                            questionVoice = it.questionVoice,
                        )
                    },
            )
        val saved = quizItemRepository.save(item)
        return QuizItemResponse.from(saved)
    }

    @Transactional
    fun createItems(
        categoryId: String,
        items: List<CreateQuizItemPayload>,
    ): List<QuizItemResponse> {
        val quizItems =
            items.map { payload ->
                QuizItem(
                    categoryId = categoryId,
                    imageUrl = payload.imageUrl,
                    name = payload.name,
                    nameVoice = payload.nameVoice,
                    questions =
                        payload.questions.map {
                            Question(
                                question = it.question,
                                questionVoice = it.questionVoice,
                            )
                        },
                )
            }
        val saved = quizItemRepository.saveAll(quizItems)
        return saved.map { QuizItemResponse.from(it) }
    }

    fun getItemsByCategoryId(categoryId: String): List<QuizItemResponse> =
        quizItemRepository
            .findByCategoryIdAndDeletedAtIsNull(categoryId)
            .map { QuizItemResponse.from(it) }

    fun getItemById(id: String): QuizItemResponse {
        val item = findActiveById(id)
        return QuizItemResponse.from(item)
    }

    fun getItemByName(name: String): QuizItemResponse {
        val item =
            quizItemRepository.findByNameAndDeletedAtIsNull(name)
                ?: throw NoSuchElementException("퀴즈 아이템을 찾을 수 없습니다. 이름: $name")
        return QuizItemResponse.from(item)
    }

    fun getRandomItem(categoryId: String?): QuizItemResponse {
        val criteria = Criteria.where("deletedAt").`is`(null)
        if (categoryId != null) {
            criteria.and("categoryId").`is`(categoryId)
        }

        val aggregation =
            Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.sample(1),
            )
        val result = mongoTemplate.aggregate(aggregation, "quiz_item", QuizItem::class.java)

        val randomItem =
            result.mappedResults.firstOrNull()
                ?: throw NoSuchElementException("조건에 맞는 퀴즈 아이템이 없습니다.")

        return QuizItemResponse.from(randomItem)
    }

    @Transactional
    fun updateItem(
        id: String,
        request: UpdateQuizItemRequest,
    ): QuizItemResponse {
        val item = findActiveById(id)
        val updated =
            item.copy(
                categoryId = request.categoryId ?: item.categoryId,
                imageUrl = request.imageUrl ?: item.imageUrl,
                name = request.name ?: item.name,
                nameVoice = request.nameVoice ?: item.nameVoice,
                questions =
                    request.questions?.map {
                        Question(
                            question = it.question,
                            questionVoice = it.questionVoice,
                        )
                    } ?: item.questions,
            )
        val saved = quizItemRepository.save(updated)
        return QuizItemResponse.from(saved)
    }

    @Transactional
    fun deleteItem(id: String) {
        val item = findActiveById(id)
        quizItemRepository.save(item.copy(deletedAt = LocalDateTime.now()))
    }

    private fun findActiveById(id: String): QuizItem =
        quizItemRepository.findByIdAndDeletedAtIsNull(id)
            ?: throw NoSuchElementException("퀴즈 아이템을 찾을 수 없습니다. ID: $id")
}
