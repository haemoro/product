package com.sotti.product.dto

import com.sotti.product.domain.AppUser
import java.time.LocalDateTime

data class AppUserResponse(
    val id: String,
    val apiKey: String,
    val nickname: String?,
    val deviceId: String?,
    val platform: String?,
    val deviceModel: String?,
    val osVersion: String?,
    val locale: String?,
    val allowedCategoryIds: List<String>,
    val active: Boolean,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
) {
    companion object {
        fun from(user: AppUser): AppUserResponse =
            AppUserResponse(
                id = user.id!!,
                apiKey = user.apiKey,
                nickname = user.nickname,
                deviceId = user.deviceId,
                platform = user.platform,
                deviceModel = user.deviceModel,
                osVersion = user.osVersion,
                locale = user.locale,
                allowedCategoryIds = user.allowedCategoryIds,
                active = user.active,
                createdAt = user.createdAt,
                updatedAt = user.updatedAt,
            )
    }
}
