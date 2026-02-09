package com.sotti.product.musictrack.controller

import com.sotti.product.musictrack.dto.CheckTrackAnswerRequest
import com.sotti.product.musictrack.dto.CheckTrackAnswerResponse
import com.sotti.product.musictrack.dto.QuestionResponse
import com.sotti.product.musictrack.service.MusicTrackGameService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "MusicTrack Game", description = "음악 트랙 퀴즈 게임 API")
@RestController
@RequestMapping("/api/v1/music-quiz")
class MusicTrackGameController(
    private val musicTrackGameService: MusicTrackGameService,
) {
    @Operation(summary = "문제 생성", description = "랜덤 음악 트랙 퀴즈 문제를 생성합니다")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "문제 생성 성공"),
        ApiResponse(responseCode = "400", description = "트랙 부족"),
    )
    @GetMapping("/question")
    fun getQuestion(
        @Parameter(description = "카테고리 필터") @RequestParam(required = false) category: String?,
        @Parameter(description = "난이도 필터") @RequestParam(required = false) difficulty: String?,
        @Parameter(description = "제외할 트랙 ID 목록") @RequestParam(required = false) excludeTrackIds: List<String>?,
    ): ResponseEntity<QuestionResponse> {
        val response = musicTrackGameService.generateQuestion(category, difficulty, excludeTrackIds)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "정답 체크", description = "선택한 트랙이 정답인지 확인합니다")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "정답 체크 완료"),
        ApiResponse(responseCode = "400", description = "잘못된 토큰 또는 선택"),
    )
    @PostMapping("/answer")
    fun checkAnswer(
        @Valid @RequestBody request: CheckTrackAnswerRequest,
    ): ResponseEntity<CheckTrackAnswerResponse> {
        val response = musicTrackGameService.checkAnswer(request)
        return ResponseEntity.ok(response)
    }
}
