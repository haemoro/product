package com.sotti.product.domain

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.CompoundIndexes
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "quiz_item")
@CompoundIndexes(
    CompoundIndex(
        name = "idx_categoryId_deletedAt",
        def = "{'categoryId': 1, 'deletedAt': 1}",
    ),
    CompoundIndex(
        name = "idx_deletedAt",
        def = "{'deletedAt': 1}",
    ),
    CompoundIndex(
        name = "idx_name_deletedAt_unique",
        def = "{'name': 1, 'deletedAt': 1}",
        unique = true,
    ),
)
data class QuizItem(
    @Id
    val id: String? = null,
    val categoryId: String,
    val imageUrl: String,
    val name: String,
    val nameVoice: String,
    val questions: List<Question>,
    val deletedAt: LocalDateTime? = null,
    @CreatedDate
    val createdAt: LocalDateTime? = null,
    @LastModifiedDate
    val updatedAt: LocalDateTime? = null,
)

data class Question(
    val question: String,
    val questionVoice: String,
)
