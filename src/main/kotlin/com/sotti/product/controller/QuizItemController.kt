package com.sotti.product.controller

import com.sotti.product.dto.BulkCreateQuizItemsRequest
import com.sotti.product.dto.BulkCreateQuizItemsResponse
import com.sotti.product.dto.CreateQuizItemRequest
import com.sotti.product.dto.QuizItemResponse
import com.sotti.product.dto.UpdateQuizItemRequest
import com.sotti.product.service.QuizCategoryService
import com.sotti.product.service.QuizItemService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
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

@Tag(name = "QuizItem", description = "소띠퀴즈 아이템 API")
@RestController
@RequestMapping("/api/v1/quiz-items")
class QuizItemController(
    private val quizItemService: QuizItemService,
    private val quizCategoryService: QuizCategoryService,
) {
    @Operation(summary = "아이템 생성", description = "새로운 퀴즈 아이템을 생성합니다")
    @ApiResponses(
        ApiResponse(responseCode = "201", description = "아이템 생성 성공"),
        ApiResponse(responseCode = "400", description = "잘못된 요청"),
    )
    @PostMapping
    fun createItem(
        @Valid @RequestBody request: CreateQuizItemRequest,
    ): ResponseEntity<QuizItemResponse> {
        val response = quizItemService.createItem(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @Operation(summary = "아이템 벌크 생성", description = "카테고리 이름으로 자동 find-or-create 후 여러 퀴즈 아이템을 한 번에 생성합니다")
    @ApiResponses(
        ApiResponse(responseCode = "201", description = "벌크 생성 성공"),
        ApiResponse(responseCode = "400", description = "잘못된 요청"),
    )
    @PostMapping("/bulk")
    fun createItemsBulk(
        @Valid @RequestBody request: BulkCreateQuizItemsRequest,
    ): ResponseEntity<BulkCreateQuizItemsResponse> {
        val category = quizCategoryService.findOrCreateByName(request.categoryName, request.categoryImageUrl)
        val items = quizItemService.createItems(category.id, request.items)
        val response =
            BulkCreateQuizItemsResponse(
                categoryId = category.id,
                categoryName = category.name,
                createdCount = items.size,
                items = items,
            )
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @Operation(summary = "카테고리별 아이템 목록 조회", description = "특정 카테고리의 퀴즈 아이템을 조회합니다")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping
    fun getItemsByCategoryId(
        @Parameter(description = "카테고리 ID") @RequestParam categoryId: String,
    ): ResponseEntity<List<QuizItemResponse>> {
        val response = quizItemService.getItemsByCategoryId(categoryId)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "이름으로 아이템 조회", description = "이름으로 퀴즈 아이템을 조회합니다")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "조회 성공"),
        ApiResponse(responseCode = "404", description = "아이템을 찾을 수 없음"),
    )
    @GetMapping("/search")
    fun getItemByName(
        @Parameter(description = "아이템 이름") @RequestParam name: String,
    ): ResponseEntity<QuizItemResponse> {
        val response = quizItemService.getItemByName(name)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "아이템 단건 조회", description = "ID로 퀴즈 아이템을 조회합니다")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "조회 성공"),
        ApiResponse(responseCode = "404", description = "아이템을 찾을 수 없음"),
    )
    @GetMapping("/{id}")
    fun getItemById(
        @Parameter(description = "아이템 ID") @PathVariable id: String,
    ): ResponseEntity<QuizItemResponse> {
        val response = quizItemService.getItemById(id)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "랜덤 아이템 조회", description = "조건에 맞는 랜덤 퀴즈 아이템을 조회합니다")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "조회 성공"),
        ApiResponse(responseCode = "404", description = "조건에 맞는 아이템이 없음"),
    )
    @GetMapping("/random")
    fun getRandomItem(
        @Parameter(description = "카테고리 ID (선택)") @RequestParam(required = false) categoryId: String?,
    ): ResponseEntity<QuizItemResponse> {
        val response = quizItemService.getRandomItem(categoryId)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "아이템 수정", description = "기존 퀴즈 아이템을 수정합니다")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "수정 성공"),
        ApiResponse(responseCode = "404", description = "아이템을 찾을 수 없음"),
    )
    @PutMapping("/{id}")
    fun updateItem(
        @Parameter(description = "아이템 ID") @PathVariable id: String,
        @Valid @RequestBody request: UpdateQuizItemRequest,
    ): ResponseEntity<QuizItemResponse> {
        val response = quizItemService.updateItem(id, request)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "아이템 삭제", description = "퀴즈 아이템을 소프트 삭제합니다")
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "삭제 성공"),
        ApiResponse(responseCode = "404", description = "아이템을 찾을 수 없음"),
    )
    @DeleteMapping("/{id}")
    fun deleteItem(
        @Parameter(description = "아이템 ID") @PathVariable id: String,
    ): ResponseEntity<Void> {
        quizItemService.deleteItem(id)
        return ResponseEntity.noContent().build()
    }
}
