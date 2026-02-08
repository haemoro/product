package com.sotti.product.service

import com.sotti.product.domain.QuizCategory
import com.sotti.product.dto.CreateQuizCategoryRequest
import com.sotti.product.dto.QuizCategoryResponse
import com.sotti.product.dto.UpdateQuizCategoryRequest
import com.sotti.product.repository.QuizCategoryRepository
import com.sotti.product.repository.QuizItemRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional(readOnly = true)
class QuizCategoryService(
    private val quizCategoryRepository: QuizCategoryRepository,
    private val quizItemRepository: QuizItemRepository,
) {
    @Transactional
    fun createCategory(request: CreateQuizCategoryRequest): QuizCategoryResponse {
        val category =
            QuizCategory(
                name = request.name,
                visible = request.visible,
                displayOrder = request.displayOrder,
            )
        val saved = quizCategoryRepository.save(category)
        return QuizCategoryResponse.from(saved)
    }

    fun getVisibleCategories(): List<QuizCategoryResponse> =
        quizCategoryRepository
            .findByVisibleTrueAndDeletedAtIsNullOrderByDisplayOrderAsc()
            .map { QuizCategoryResponse.from(it) }

    fun getCategoryById(id: String): QuizCategoryResponse {
        val category = findActiveById(id)
        return QuizCategoryResponse.from(category)
    }

    @Transactional
    fun updateCategory(
        id: String,
        request: UpdateQuizCategoryRequest,
    ): QuizCategoryResponse {
        val category = findActiveById(id)
        val updated =
            category.copy(
                name = request.name ?: category.name,
                imageUrl = request.imageUrl ?: category.imageUrl,
                visible = request.visible ?: category.visible,
                displayOrder = request.displayOrder ?: category.displayOrder,
            )
        val saved = quizCategoryRepository.save(updated)
        return QuizCategoryResponse.from(saved)
    }

    @Transactional
    fun deleteCategory(id: String) {
        val category = findActiveById(id)
        quizCategoryRepository.save(category.copy(deletedAt = LocalDateTime.now()))
    }

    @Transactional
    fun findOrCreateByName(
        name: String,
        imageUrl: String? = null,
    ): QuizCategoryResponse {
        val existing = quizCategoryRepository.findByNameAndDeletedAtIsNull(name)
        if (existing != null) {
            if (imageUrl != null && existing.imageUrl != imageUrl) {
                val updated = quizCategoryRepository.save(existing.copy(imageUrl = imageUrl))
                return QuizCategoryResponse.from(updated)
            }
            return QuizCategoryResponse.from(existing)
        }

        val category =
            QuizCategory(
                name = name,
                imageUrl = imageUrl,
                visible = true,
                displayOrder = 0,
            )
        val saved = quizCategoryRepository.save(category)
        return QuizCategoryResponse.from(saved)
    }

    @Transactional
    fun updateCategoryImageByItemName(
        categoryName: String,
        itemName: String,
    ): QuizCategoryResponse {
        val category =
            quizCategoryRepository.findByNameAndDeletedAtIsNull(categoryName)
                ?: throw NoSuchElementException("카테고리를 찾을 수 없습니다. 이름: $categoryName")
        val item =
            quizItemRepository.findByNameAndDeletedAtIsNull(itemName)
                ?: throw NoSuchElementException("아이템을 찾을 수 없습니다. 이름: $itemName")
        val updated = quizCategoryRepository.save(category.copy(imageUrl = item.imageUrl))
        return QuizCategoryResponse.from(updated)
    }

    private fun findActiveById(id: String): QuizCategory =
        quizCategoryRepository.findByIdAndDeletedAtIsNull(id)
            ?: throw NoSuchElementException("카테고리를 찾을 수 없습니다. ID: $id")
}
