package com.sotti.product.musictrack.controller

import com.sotti.product.musictrack.domain.TrackStatus
import com.sotti.product.musictrack.dto.CreateMusicTrackRequest
import com.sotti.product.musictrack.dto.MusicTrackResponse
import com.sotti.product.musictrack.dto.UpdateMusicTrackRequest
import com.sotti.product.musictrack.service.MusicTrackService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "MusicTrack Admin", description = "음악 트랙 관리 API (어드민)")
@RestController
@RequestMapping("/api/v1/music-quiz/admin/tracks")
class MusicTrackAdminController(
    private val musicTrackService: MusicTrackService,
) {
    @Operation(summary = "트랙 생성", description = "새로운 음악 트랙을 등록합니다")
    @ApiResponses(
        ApiResponse(responseCode = "201", description = "트랙 생성 성공"),
        ApiResponse(responseCode = "400", description = "잘못된 요청 또는 중복된 YouTube 동영상"),
    )
    @PostMapping
    fun createTrack(
        @Valid @RequestBody request: CreateMusicTrackRequest,
    ): ResponseEntity<MusicTrackResponse> {
        val response = musicTrackService.createTrack(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @Operation(summary = "트랙 목록 조회", description = "음악 트랙 목록을 조회합니다 (status, category 필터)")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping
    fun getTracks(
        @Parameter(description = "트랙 상태 필터") @RequestParam(required = false) status: TrackStatus?,
        @Parameter(description = "카테고리 필터") @RequestParam(required = false) category: String?,
    ): ResponseEntity<List<MusicTrackResponse>> {
        val response = musicTrackService.getTracks(status, category)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "트랙 수정", description = "기존 음악 트랙을 부분 수정합니다")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "수정 성공"),
        ApiResponse(responseCode = "404", description = "트랙을 찾을 수 없음"),
    )
    @PatchMapping("/{trackId}")
    fun updateTrack(
        @Parameter(description = "트랙 ID") @PathVariable trackId: String,
        @RequestBody request: UpdateMusicTrackRequest,
    ): ResponseEntity<MusicTrackResponse> {
        val response = musicTrackService.updateTrack(trackId, request)
        return ResponseEntity.ok(response)
    }
}
