package com.sotti.product.controller

import com.sotti.product.dto.AppUserResponse
import com.sotti.product.dto.UpdateAllowedCategoriesRequest
import com.sotti.product.service.AppUserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Admin", description = "어드민 유저 관리 API")
@RestController
@RequestMapping("/api/v1/admin")
class AdminController(
    private val appUserService: AppUserService,
) {
    @Operation(summary = "전체 유저 목록", description = "등록된 전체 유저 목록을 조회합니다")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/users")
    fun getAllUsers(): ResponseEntity<List<AppUserResponse>> {
        val response = appUserService.getAllUsers()
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "유저 상세 조회", description = "ID로 유저 상세 정보를 조회합니다")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "조회 성공"),
        ApiResponse(responseCode = "404", description = "유저를 찾을 수 없음"),
    )
    @GetMapping("/users/{id}")
    fun getUserById(
        @Parameter(description = "유저 ID") @PathVariable id: String,
    ): ResponseEntity<AppUserResponse> {
        val response = appUserService.getUserById(id)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "유저 카테고리 할당", description = "유저에게 접근 가능한 카테고리를 할당합니다")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "할당 성공"),
        ApiResponse(responseCode = "404", description = "유저를 찾을 수 없음"),
    )
    @PutMapping("/users/{id}/categories")
    fun updateAllowedCategories(
        @Parameter(description = "유저 ID") @PathVariable id: String,
        @Valid @RequestBody request: UpdateAllowedCategoriesRequest,
    ): ResponseEntity<AppUserResponse> {
        val response = appUserService.updateAllowedCategories(id, request.categoryIds)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "유저 허용 카테고리 조회", description = "유저에게 허용된 카테고리 ID 목록을 조회합니다")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "조회 성공"),
        ApiResponse(responseCode = "404", description = "유저를 찾을 수 없음"),
    )
    @GetMapping("/users/{id}/categories")
    fun getUserAllowedCategories(
        @Parameter(description = "유저 ID") @PathVariable id: String,
    ): ResponseEntity<List<String>> {
        val response = appUserService.getUserAllowedCategories(id)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "유저 비활성화", description = "유저를 비활성화합니다")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "비활성화 성공"),
        ApiResponse(responseCode = "404", description = "유저를 찾을 수 없음"),
    )
    @DeleteMapping("/users/{id}")
    fun deactivateUser(
        @Parameter(description = "유저 ID") @PathVariable id: String,
    ): ResponseEntity<AppUserResponse> {
        val response = appUserService.deactivateUser(id)
        return ResponseEntity.ok(response)
    }
}
