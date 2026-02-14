package com.sotti.product.service

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.sotti.product.domain.AppUser
import com.sotti.product.domain.QuizCategory
import com.sotti.product.dto.CreateQuizCategoryRequest
import com.sotti.product.dto.QuizCategoryResponse
import com.sotti.product.dto.UpdateQuizCategoryRequest
import com.sotti.product.repository.QuizCategoryRepository
import com.sotti.product.repository.QuizItemRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

@Service
@Transactional(readOnly = true)
class QuizCategoryService(
    private val quizCategoryRepository: QuizCategoryRepository,
    private val quizItemRepository: QuizItemRepository,
) {
    private val categoryCache: Cache<String, List<QuizCategory>> =
        Caffeine
            .newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .maximumSize(2)
            .build()

    companion object {
        private const val CACHE_KEY_VISIBLE = "visible"
        private const val CACHE_KEY_ALL = "all"
    }

    @Transactional
    fun createCategory(request: CreateQuizCategoryRequest): QuizCategoryResponse {
        val category =
            QuizCategory(
                name = request.name,
                visible = request.visible,
                displayOrder = request.displayOrder,
            )
        val saved = quizCategoryRepository.save(category)
        evictCache()
        return QuizCategoryResponse.from(saved)
    }

    fun getVisibleCategories(user: AppUser? = null): List<QuizCategoryResponse> {
        val categories =
            categoryCache.get(CACHE_KEY_VISIBLE) {
                quizCategoryRepository.findByVisibleTrueAndDeletedAtIsNullOrderByDisplayOrderAsc()
            }!!

        val filtered =
            if (user != null && user.allowedCategoryIds.isNotEmpty()) {
                categories.filter { it.id in user.allowedCategoryIds }
            } else {
                categories
            }

        return filtered.map { QuizCategoryResponse.from(it) }
    }

    fun getAllCategories(): List<QuizCategoryResponse> {
        val categories =
            categoryCache.get(CACHE_KEY_ALL) {
                quizCategoryRepository
                    .findAll()
                    .filter { it.deletedAt == null }
                    .sortedBy { it.displayOrder }
            }!!
        return categories.map { QuizCategoryResponse.from(it) }
    }

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
        evictCache()
        return QuizCategoryResponse.from(saved)
    }

    @Transactional
    fun deleteCategory(id: String) {
        val category = findActiveById(id)
        quizCategoryRepository.save(category.copy(deletedAt = LocalDateTime.now()))
        evictCache()
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
                evictCache()
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
        evictCache()
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
        evictCache()
        return QuizCategoryResponse.from(updated)
    }

    private fun findActiveById(id: String): QuizCategory =
        quizCategoryRepository.findByIdAndDeletedAtIsNull(id)
            ?: throw NoSuchElementException("카테고리를 찾을 수 없습니다. ID: $id")

    private fun evictCache() {
        categoryCache.invalidateAll()
    }
}
