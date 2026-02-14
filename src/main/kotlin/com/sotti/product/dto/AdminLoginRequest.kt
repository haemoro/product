package com.sotti.product.dto

import jakarta.validation.constraints.NotBlank

data class AdminLoginRequest(
    @field:NotBlank(message = "비밀번호를 입력해주세요")
    val password: String,
)
