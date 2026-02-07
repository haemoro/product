package com.sotti.product.domain

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.CompoundIndexes
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "quiz_category")
@CompoundIndexes(
    CompoundIndex(
        name = "idx_visible_deletedAt_displayOrder",
        def = "{'visible': 1, 'deletedAt': 1, 'displayOrder': 1}",
    ),
    CompoundIndex(
        name = "idx_name_deletedAt",
        def = "{'name': 1, 'deletedAt': 1}",
    ),
)
data class QuizCategory(
    @Id
    val id: String? = null,
    val name: String,
    val imageUrl: String? = null,
    val visible: Boolean,
    val displayOrder: Int = 0,
    val deletedAt: LocalDateTime? = null,
    @CreatedDate
    val createdAt: LocalDateTime? = null,
    @LastModifiedDate
    val updatedAt: LocalDateTime? = null,
)
