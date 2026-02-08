package com.sotti.product.dto

data class RegisterUserRequest(
    val nickname: String? = null,
    val deviceId: String? = null,
    val platform: String? = null,
)
