package com.sotti.product.service

import com.sotti.product.domain.AppUser
import com.sotti.product.dto.AppUserResponse
import com.sotti.product.dto.RegisterUserRequest
import com.sotti.product.dto.RegisterUserResponse
import com.sotti.product.repository.AppUserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional(readOnly = true)
class AppUserService(
    private val appUserRepository: AppUserRepository,
) {
    @Transactional
    fun register(request: RegisterUserRequest): RegisterUserResponse {
        if (request.deviceId != null) {
            val existing = appUserRepository.findByDeviceId(request.deviceId)
            if (existing != null) {
                return RegisterUserResponse.from(existing)
            }
        }

        val user =
            AppUser(
                apiKey = UUID.randomUUID().toString(),
                nickname = request.nickname,
                deviceId = request.deviceId,
                platform = request.platform,
                deviceModel = request.deviceModel,
                osVersion = request.osVersion,
                locale = request.locale,
            )
        val saved = appUserRepository.save(user)
        return RegisterUserResponse.from(saved)
    }

    fun findByApiKey(apiKey: String): AppUser? = appUserRepository.findByApiKey(apiKey)

    fun getAllUsers(): List<AppUserResponse> = appUserRepository.findAll().map { AppUserResponse.from(it) }

    fun getUserById(id: String): AppUserResponse {
        val user =
            appUserRepository
                .findById(id)
                .orElseThrow { NoSuchElementException("유저를 찾을 수 없습니다. ID: $id") }
        return AppUserResponse.from(user)
    }

    @Transactional
    fun updateAllowedCategories(
        userId: String,
        categoryIds: List<String>,
    ): AppUserResponse {
        val user =
            appUserRepository
                .findById(userId)
                .orElseThrow { NoSuchElementException("유저를 찾을 수 없습니다. ID: $userId") }
        val updated = appUserRepository.save(user.copy(allowedCategoryIds = categoryIds))
        return AppUserResponse.from(updated)
    }

    fun getUserAllowedCategories(userId: String): List<String> {
        val user =
            appUserRepository
                .findById(userId)
                .orElseThrow { NoSuchElementException("유저를 찾을 수 없습니다. ID: $userId") }
        return user.allowedCategoryIds
    }

    @Transactional
    fun deactivateUser(userId: String): AppUserResponse {
        val user =
            appUserRepository
                .findById(userId)
                .orElseThrow { NoSuchElementException("유저를 찾을 수 없습니다. ID: $userId") }
        val updated = appUserRepository.save(user.copy(active = false))
        return AppUserResponse.from(updated)
    }
}
