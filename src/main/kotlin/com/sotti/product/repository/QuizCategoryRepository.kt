package com.sotti.product.repository

import com.sotti.product.domain.QuizCategory
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface QuizCategoryRepository : MongoRepository<QuizCategory, String> {
    fun findByVisibleTrueAndDeletedAtIsNullOrderByDisplayOrderAsc(): List<QuizCategory>

    fun findByIdAndDeletedAtIsNull(id: String): QuizCategory?

    fun findByNameAndDeletedAtIsNull(name: String): QuizCategory?
}
