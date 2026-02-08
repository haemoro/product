package com.sotti.product.controller

import com.sotti.product.dto.RegisterUserRequest
import com.sotti.product.dto.RegisterUserResponse
import com.sotti.product.service.AppUserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "User", description = "유저 등록 API")
@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val appUserService: AppUserService,
) {
    @Operation(summary = "유저 등록", description = "앱 첫 실행 시 유저를 등록하고 API Key를 발급합니다")
    @ApiResponses(
        ApiResponse(responseCode = "201", description = "유저 등록 성공"),
        ApiResponse(responseCode = "200", description = "기존 유저 반환 (동일 deviceId)"),
    )
    @PostMapping("/register")
    fun register(
        @RequestBody request: RegisterUserRequest,
    ): ResponseEntity<RegisterUserResponse> {
        val response = appUserService.register(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }
}
