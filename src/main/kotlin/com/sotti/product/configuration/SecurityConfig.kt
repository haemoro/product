package com.sotti.product.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import com.sotti.product.service.AppUserService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val corsConfigurationSource: CorsConfigurationSource,
    private val appUserService: AppUserService,
    private val objectMapper: ObjectMapper,
    @Value("\${app.admin-api-key:#{null}}")
    private val adminApiKey: String?,
) {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors { it.configurationSource(corsConfigurationSource) }
            .csrf { it.disable() }
            .addFilterBefore(
                ApiKeyAuthFilter(appUserService, adminApiKey ?: "", objectMapper),
                UsernamePasswordAuthenticationFilter::class.java,
            ).authorizeHttpRequests { auth ->
                auth.anyRequest().permitAll()
            }
        return http.build()
    }
}
