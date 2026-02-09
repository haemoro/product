package com.sotti.product.musictrack.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.Base64

@Service
class QuestionTokenService(
    private val objectMapper: ObjectMapper,
) {
    companion object {
        private const val TOKEN_TTL_SECONDS = 600L // 10분
    }

    fun generate(
        correctTrackId: String,
        choiceTrackIds: List<String>,
    ): String {
        val now = Instant.now().epochSecond
        val payload =
            mapOf(
                "cid" to correctTrackId,
                "choices" to choiceTrackIds,
                "iat" to now,
                "exp" to now + TOKEN_TTL_SECONDS,
            )
        val json = objectMapper.writeValueAsBytes(payload)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(json)
    }

    fun decode(token: String): TokenPayload {
        val json =
            try {
                Base64.getUrlDecoder().decode(token)
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("유효하지 않은 토큰입니다.")
            }

        val map =
            try {
                @Suppress("UNCHECKED_CAST")
                objectMapper.readValue(json, Map::class.java) as Map<String, Any>
            } catch (e: Exception) {
                throw IllegalArgumentException("유효하지 않은 토큰입니다.")
            }

        val cid = map["cid"] as? String ?: throw IllegalArgumentException("유효하지 않은 토큰입니다.")
        val choices =
            (map["choices"] as? List<*>)?.filterIsInstance<String>()
                ?: throw IllegalArgumentException("유효하지 않은 토큰입니다.")
        val exp = (map["exp"] as? Number)?.toLong() ?: throw IllegalArgumentException("유효하지 않은 토큰입니다.")

        if (Instant.now().epochSecond > exp) {
            throw IllegalArgumentException("만료된 토큰입니다.")
        }

        return TokenPayload(
            correctTrackId = cid,
            choiceTrackIds = choices,
        )
    }

    data class TokenPayload(
        val correctTrackId: String,
        val choiceTrackIds: List<String>,
    )
}
