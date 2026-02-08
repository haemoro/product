package com.sotti.product.domain

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "app_user")
data class AppUser(
    @Id
    val id: String? = null,
    @Indexed(unique = true)
    val apiKey: String,
    val nickname: String? = null,
    @Indexed(unique = true, sparse = true)
    val deviceId: String? = null,
    val platform: String? = null,
    val allowedCategoryIds: List<String> = emptyList(),
    val active: Boolean = true,
    @CreatedDate
    val createdAt: LocalDateTime? = null,
    @LastModifiedDate
    val updatedAt: LocalDateTime? = null,
)
