package com.sotti.product.dto

import com.sotti.product.domain.AppUser

data class RegisterUserResponse(
    val id: String,
    val apiKey: String,
    val nickname: String?,
) {
    companion object {
        fun from(user: AppUser): RegisterUserResponse =
            RegisterUserResponse(
                id = user.id!!,
                apiKey = user.apiKey,
                nickname = user.nickname,
            )
    }
}
