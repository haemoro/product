package com.sotti.product.dto

import java.time.LocalDateTime

data class ErrorResponse(
    val status: Int,
    val message: String,
    val timestamp: String = LocalDateTime.now().toString(),
)
