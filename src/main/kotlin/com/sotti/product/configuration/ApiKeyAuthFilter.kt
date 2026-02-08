package com.sotti.product.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import com.sotti.product.service.AppUserService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.web.filter.OncePerRequestFilter

class ApiKeyAuthFilter(
    private val appUserService: AppUserService,
    private val adminApiKey: String,
    private val objectMapper: ObjectMapper,
) : OncePerRequestFilter() {
    companion object {
        private val PUBLIC_PATHS =
            listOf(
                "/api/v1/users/register",
                "/actuator/health",
                "/swagger-ui",
                "/v3/api-docs",
            )
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val path = request.requestURI

        if (isPublicPath(path)) {
            filterChain.doFilter(request, response)
            return
        }

        // X-Admin-Key가 유효하면 모든 경로 접근 허용 (어드민 슈퍼 권한)
        val adminKey = request.getHeader("X-Admin-Key")
        if (!adminKey.isNullOrBlank() && adminKey == adminApiKey) {
            filterChain.doFilter(request, response)
            return
        }

        if (isAdminPath(path)) {
            handleAdminAuth(request, response, filterChain)
            return
        }

        val apiKey = request.getHeader("X-API-Key")
        if (apiKey == null) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "API Key가 필요합니다")
            return
        }

        val user = appUserService.findByApiKey(apiKey)
        if (user == null || !user.active) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 API Key입니다")
            return
        }

        request.setAttribute("currentUser", user)
        filterChain.doFilter(request, response)
    }

    private fun isPublicPath(path: String): Boolean = PUBLIC_PATHS.any { path.startsWith(it) }

    private fun isAdminPath(path: String): Boolean = path.startsWith("/api/v1/admin")

    private fun handleAdminAuth(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val key = request.getHeader("X-Admin-Key")
        if (key == null || key != adminApiKey) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 Admin Key입니다")
            return
        }
        filterChain.doFilter(request, response)
    }

    private fun sendError(
        response: HttpServletResponse,
        status: Int,
        message: String,
    ) {
        response.status = status
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = "UTF-8"
        val body = mapOf("error" to message)
        response.writer.write(objectMapper.writeValueAsString(body))
    }
}
