package com.sotti.product.controller

import com.sotti.product.dto.AppUserResponse
import com.sotti.product.dto.CreateQuizCategoryRequest
import com.sotti.product.dto.CreateQuizItemRequest
import com.sotti.product.dto.ImageUploadResponse
import com.sotti.product.dto.QuizCategoryResponse
import com.sotti.product.dto.QuizItemResponse
import com.sotti.product.dto.UpdateAllowedCategoriesRequest
import com.sotti.product.dto.UpdateQuizCategoryRequest
import com.sotti.product.dto.UpdateQuizItemRequest
import com.sotti.product.service.AppUserService
import com.sotti.product.service.ImageUploadService
import com.sotti.product.service.QuizCategoryService
import com.sotti.product.service.QuizItemService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@Tag(name = "Admin", description = "어드민 API")
@RestController
@RequestMapping("/api/v1/admin")
class AdminController(
    private val appUserService: AppUserService,
    private val quizCategoryService: QuizCategoryService,
    private val quizItemService: QuizItemService,
    private val imageUploadService: ImageUploadService? = null,
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

    // ==================== 카테고리 관리 ====================

    @Operation(summary = "전체 카테고리 목록", description = "삭제되지 않은 모든 카테고리를 조회합니다")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/quiz-categories")
    fun getAllCategories(): ResponseEntity<List<QuizCategoryResponse>> {
        val response = quizCategoryService.getAllCategories()
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "카테고리 단건 조회", description = "ID로 카테고리를 조회합니다")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/quiz-categories/{id}")
    fun getCategoryById(
        @Parameter(description = "카테고리 ID") @PathVariable id: String,
    ): ResponseEntity<QuizCategoryResponse> {
        val response = quizCategoryService.getCategoryById(id)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "카테고리 생성", description = "새로운 퀴즈 카테고리를 생성합니다")
    @ApiResponse(responseCode = "201", description = "생성 성공")
    @PostMapping("/quiz-categories")
    fun createCategory(
        @Valid @RequestBody request: CreateQuizCategoryRequest,
    ): ResponseEntity<QuizCategoryResponse> {
        val response = quizCategoryService.createCategory(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @Operation(summary = "카테고리 수정", description = "기존 카테고리를 수정합니다")
    @ApiResponse(responseCode = "200", description = "수정 성공")
    @PutMapping("/quiz-categories/{id}")
    fun updateCategory(
        @Parameter(description = "카테고리 ID") @PathVariable id: String,
        @Valid @RequestBody request: UpdateQuizCategoryRequest,
    ): ResponseEntity<QuizCategoryResponse> {
        val response = quizCategoryService.updateCategory(id, request)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "카테고리 삭제", description = "카테고리를 소프트 삭제합니다")
    @ApiResponse(responseCode = "204", description = "삭제 성공")
    @DeleteMapping("/quiz-categories/{id}")
    fun deleteCategory(
        @Parameter(description = "카테고리 ID") @PathVariable id: String,
    ): ResponseEntity<Void> {
        quizCategoryService.deleteCategory(id)
        return ResponseEntity.noContent().build()
    }

    // ==================== 퀴즈 아이템 관리 ====================

    @Operation(summary = "카테고리별 아이템 목록", description = "특정 카테고리의 퀴즈 아이템을 조회합니다")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/quiz-items")
    fun getItemsByCategoryId(
        @Parameter(description = "카테고리 ID") @RequestParam categoryId: String,
    ): ResponseEntity<List<QuizItemResponse>> {
        val response = quizItemService.getItemsByCategoryId(categoryId)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "아이템 단건 조회", description = "ID로 퀴즈 아이템을 조회합니다")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/quiz-items/{id}")
    fun getItemById(
        @Parameter(description = "아이템 ID") @PathVariable id: String,
    ): ResponseEntity<QuizItemResponse> {
        val response = quizItemService.getItemById(id)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "아이템 생성", description = "새로운 퀴즈 아이템을 생성합니다")
    @ApiResponse(responseCode = "201", description = "생성 성공")
    @PostMapping("/quiz-items")
    fun createItem(
        @Valid @RequestBody request: CreateQuizItemRequest,
    ): ResponseEntity<QuizItemResponse> {
        val response = quizItemService.createItem(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @Operation(summary = "아이템 수정", description = "기존 퀴즈 아이템을 수정합니다")
    @ApiResponse(responseCode = "200", description = "수정 성공")
    @PutMapping("/quiz-items/{id}")
    fun updateItem(
        @Parameter(description = "아이템 ID") @PathVariable id: String,
        @Valid @RequestBody request: UpdateQuizItemRequest,
    ): ResponseEntity<QuizItemResponse> {
        val response = quizItemService.updateItem(id, request)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "아이템 삭제", description = "퀴즈 아이템을 소프트 삭제합니다")
    @ApiResponse(responseCode = "204", description = "삭제 성공")
    @DeleteMapping("/quiz-items/{id}")
    fun deleteItem(
        @Parameter(description = "아이템 ID") @PathVariable id: String,
    ): ResponseEntity<Void> {
        quizItemService.deleteItem(id)
        return ResponseEntity.noContent().build()
    }

    // ==================== 이미지 업로드 ====================

    @Operation(summary = "이미지 업로드", description = "이미지를 Cloudflare R2에 업로드하고 공개 URL을 반환합니다")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "업로드 성공"),
        ApiResponse(responseCode = "400", description = "잘못된 파일 형식 또는 크기 초과"),
    )
    @PostMapping("/images/upload", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadImage(
        @Parameter(description = "이미지 파일 (JPEG, PNG, WebP, GIF)") @RequestPart file: MultipartFile,
        @Parameter(description = "저장 폴더 (기본값: images)") @RequestParam(defaultValue = "images") folder: String,
    ): ResponseEntity<ImageUploadResponse> {
        val service =
            imageUploadService
                ?: throw IllegalStateException("이미지 업로드 기능이 설정되지 않았습니다. R2 환경변수를 확인해주세요.")
        val imageUrl = service.upload(file, folder)
        return ResponseEntity.ok(ImageUploadResponse(imageUrl))
    }
}
