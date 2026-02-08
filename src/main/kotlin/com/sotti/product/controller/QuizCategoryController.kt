package com.sotti.product.controller

import com.sotti.product.domain.AppUser
import com.sotti.product.dto.CreateQuizCategoryRequest
import com.sotti.product.dto.QuizCategoryResponse
import com.sotti.product.dto.UpdateQuizCategoryRequest
import com.sotti.product.service.QuizCategoryService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "QuizCategory", description = "소띠퀴즈 카테고리 API")
@RestController
@RequestMapping("/api/v1/quiz-categories")
class QuizCategoryController(
    private val quizCategoryService: QuizCategoryService,
) {
    @Operation(summary = "카테고리 생성", description = "새로운 퀴즈 카테고리를 생성합니다")
    @ApiResponses(
        ApiResponse(responseCode = "201", description = "카테고리 생성 성공"),
        ApiResponse(responseCode = "400", description = "잘못된 요청"),
    )
    @PostMapping
    fun createCategory(
        @Valid @RequestBody request: CreateQuizCategoryRequest,
    ): ResponseEntity<QuizCategoryResponse> {
        val response = quizCategoryService.createCategory(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @Operation(summary = "카테고리 목록 조회", description = "유저에게 허용된 카테고리 목록을 정렬 순서대로 조회합니다")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping
    fun getVisibleCategories(request: HttpServletRequest): ResponseEntity<List<QuizCategoryResponse>> {
        val user = request.getAttribute("currentUser") as? AppUser
        val response = quizCategoryService.getVisibleCategories(user)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "카테고리 단건 조회", description = "ID로 카테고리를 조회합니다")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "조회 성공"),
        ApiResponse(responseCode = "404", description = "카테고리를 찾을 수 없음"),
    )
    @GetMapping("/{id}")
    fun getCategoryById(
        @Parameter(description = "카테고리 ID") @PathVariable id: String,
    ): ResponseEntity<QuizCategoryResponse> {
        val response = quizCategoryService.getCategoryById(id)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "카테고리 수정", description = "기존 퀴즈 카테고리를 수정합니다")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "수정 성공"),
        ApiResponse(responseCode = "404", description = "카테고리를 찾을 수 없음"),
    )
    @PutMapping("/{id}")
    fun updateCategory(
        @Parameter(description = "카테고리 ID") @PathVariable id: String,
        @Valid @RequestBody request: UpdateQuizCategoryRequest,
    ): ResponseEntity<QuizCategoryResponse> {
        val response = quizCategoryService.updateCategory(id, request)
        return ResponseEntity.ok(response)
    }

    @Operation(
        summary = "카테고리 이미지를 아이템 이미지로 변경",
        description = "카테고리 이름과 아이템 이름을 받아 해당 아이템의 이미지를 카테고리 대표 이미지로 설정합니다",
    )
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "수정 성공"),
        ApiResponse(responseCode = "404", description = "카테고리 또는 아이템을 찾을 수 없음"),
    )
    @PutMapping("/4image-from-item")
    fun updateCategoryImageByItemName(
        @Parameter(description = "카테고리 이름") @RequestParam categoryName: String,
        @Parameter(description = "아이템 이름") @RequestParam itemName: String,
    ): ResponseEntity<QuizCategoryResponse> {
        val response = quizCategoryService.updateCategoryImageByItemName(categoryName, itemName)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "카테고리 삭제", description = "카테고리를 소프트 삭제합니다")
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "삭제 성공"),
        ApiResponse(responseCode = "404", description = "카테고리를 찾을 수 없음"),
    )
    @DeleteMapping("/{id}")
    fun deleteCategory(
        @Parameter(description = "카테고리 ID") @PathVariable id: String,
    ): ResponseEntity<Void> {
        quizCategoryService.deleteCategory(id)
        return ResponseEntity.noContent().build()
    }
}
