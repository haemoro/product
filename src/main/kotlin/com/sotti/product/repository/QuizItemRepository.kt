package com.sotti.product.repository

import com.sotti.product.domain.QuizItem
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface QuizItemRepository : MongoRepository<QuizItem, String> {
    fun findByCategoryIdAndDeletedAtIsNull(categoryId: String): List<QuizItem>

    fun findByIdAndDeletedAtIsNull(id: String): QuizItem?

    fun findByDeletedAtIsNull(): List<QuizItem>
}
