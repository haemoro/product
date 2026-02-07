package com.sotti.product.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@Tag(name = "Health", description = "서버 상태 확인 API")
@RestController
@RequestMapping("/api/v1")
class HealthController {
    @Operation(summary = "서버 상태 확인", description = "API 서버의 상태를 확인합니다")
    @ApiResponse(responseCode = "200", description = "서버 정상 동작")
    @GetMapping("/health")
    fun health(): ResponseEntity<Map<String, Any>> {
        val healthStatus =
            mapOf(
                "status" to "UP",
                "timestamp" to LocalDateTime.now().toString(),
                "service" to "소띠퀴즈 API 서버",
                "version" to "v1.0.0",
            )
        return ResponseEntity.ok(healthStatus)
    }
}
