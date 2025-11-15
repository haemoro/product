package com.sotti.product.controller

import com.sotti.product.domain.Category
import com.sotti.product.dto.CheckAnswerRequest
import com.sotti.product.dto.CheckAnswerResponse
import com.sotti.product.dto.CreateMusicQuizRequest
import com.sotti.product.dto.MusicQuizGameResponse
import com.sotti.product.dto.MusicQuizResponse
import com.sotti.product.dto.UpdateMusicQuizRequest
import com.sotti.product.service.MusicQuizService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
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

@Tag(name = "MusicQuiz", description = "소띠뮤직 퀴즈 API")
@RestController
@RequestMapping("/api/v1/music-quiz")
class MusicQuizController(
    private val musicQuizService: MusicQuizService,
) {
    @Operation(summary = "퀴즈 생성", description = "새로운 음악 퀴즈를 생성합니다")
    @ApiResponses(
        ApiResponse(responseCode = "201", description = "퀴즈 생성 성공"),
        ApiResponse(responseCode = "400", description = "잘못된 요청"),
    )
    @PostMapping
    fun createQuiz(
        @Valid @RequestBody request: CreateMusicQuizRequest,
    ): ResponseEntity<MusicQuizResponse> {
        val response = musicQuizService.createQuiz(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @Operation(summary = "퀴즈 단건 조회", description = "ID로 퀴즈를 조회합니다 (관리자용)")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "조회 성공"),
        ApiResponse(responseCode = "404", description = "퀴즈를 찾을 수 없음"),
    )
    @GetMapping("/{id}")
    fun getQuizById(
        @Parameter(description = "퀴즈 ID") @PathVariable id: String,
    ): ResponseEntity<MusicQuizResponse> {
        val response = musicQuizService.getQuizById(id)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "게임용 퀴즈 조회", description = "게임 플레이를 위한 퀴즈 조회 (정답 숨김)")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "조회 성공"),
        ApiResponse(responseCode = "404", description = "퀴즈를 찾을 수 없음"),
    )
    @GetMapping("/game/{id}")
    fun getQuizForGame(
        @Parameter(description = "퀴즈 ID") @PathVariable id: String,
    ): ResponseEntity<MusicQuizGameResponse> {
        val response = musicQuizService.getQuizForGame(id)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "전체 퀴즈 목록 조회", description = "페이징된 퀴즈 목록을 조회합니다")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping
    fun getAllQuizzes(
        @Parameter(description = "페이지 정보")
        @PageableDefault(size = 20, sort = ["createdAt"]) pageable: Pageable,
    ): ResponseEntity<Page<MusicQuizResponse>> {
        val response = musicQuizService.getAllQuizzes(pageable)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "카테고리별 퀴즈 조회", description = "특정 카테고리의 퀴즈를 조회합니다")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/category/{category}")
    fun getQuizzesByCategory(
        @Parameter(description = "카테고리") @PathVariable category: Category,
    ): ResponseEntity<List<MusicQuizResponse>> {
        val response = musicQuizService.getQuizzesByCategory(category)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "랜덤 퀴즈 조회", description = "조건에 맞는 랜덤 퀴즈를 조회합니다")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "조회 성공"),
        ApiResponse(responseCode = "404", description = "조건에 맞는 퀴즈가 없음"),
    )
    @GetMapping("/random")
    fun getRandomQuiz(
        @Parameter(description = "카테고리 (선택)") @RequestParam(required = false) category: Category?,
    ): ResponseEntity<MusicQuizGameResponse> {
        val response = musicQuizService.getRandomQuiz(category)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "퀴즈 수정", description = "기존 퀴즈를 수정합니다")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "수정 성공"),
        ApiResponse(responseCode = "404", description = "퀴즈를 찾을 수 없음"),
    )
    @PutMapping("/{id}")
    fun updateQuiz(
        @Parameter(description = "퀴즈 ID") @PathVariable id: String,
        @Valid @RequestBody request: UpdateMusicQuizRequest,
    ): ResponseEntity<MusicQuizResponse> {
        val response = musicQuizService.updateQuiz(id, request)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "퀴즈 삭제", description = "퀴즈를 완전히 삭제합니다")
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "삭제 성공"),
        ApiResponse(responseCode = "404", description = "퀴즈를 찾을 수 없음"),
    )
    @DeleteMapping("/{id}")
    fun deleteQuiz(
        @Parameter(description = "퀴즈 ID") @PathVariable id: String,
    ): ResponseEntity<Void> {
        musicQuizService.deleteQuiz(id)
        return ResponseEntity.noContent().build()
    }

    @Operation(summary = "정답 체크", description = "사용자의 답안을 체크합니다")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "체크 완료"),
        ApiResponse(responseCode = "404", description = "퀴즈를 찾을 수 없음"),
    )
    @PostMapping("/check-answer")
    fun checkAnswer(
        @Valid @RequestBody request: CheckAnswerRequest,
    ): ResponseEntity<CheckAnswerResponse> {
        val response = musicQuizService.checkAnswer(request)
        return ResponseEntity.ok(response)
    }
}
