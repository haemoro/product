package com.sotti.product.controller

import com.sotti.product.dto.AdminLoginRequest
import com.sotti.product.dto.ErrorResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Admin Auth", description = "어드민 인증 API")
@RestController
@RequestMapping("/api/v1/admin/auth")
class AdminAuthController(
    @Value("\${app.admin-password}")
    private val adminPassword: String,
) {
    @Operation(summary = "어드민 로그인", description = "비밀번호로 어드민 로그인을 수행합니다")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "로그인 성공"),
        ApiResponse(responseCode = "401", description = "비밀번호 불일치"),
    )
    @PostMapping("/login")
    fun login(
        @Valid @RequestBody request: AdminLoginRequest,
    ): ResponseEntity<Any> {
        if (request.password != adminPassword) {
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse(status = 401, message = "비밀번호가 일치하지 않습니다"))
        }
        return ResponseEntity.ok(mapOf("authenticated" to true))
    }
}
