package com.sotti.product.configuration

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.ktor.client.HttpClient
import io.ktor.client.engine.java.Java
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.serialization.jackson.jackson
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class KtorClientConfiguration(
    @Value("\${http.timeout.default}") private val timeout: Long,
) {
    @Bean
    fun httpClient() =
        HttpClient(Java) {
            expectSuccess = true
            install(HttpTimeout) {
                connectTimeoutMillis = timeout
                socketTimeoutMillis = timeout
                requestTimeoutMillis = timeout
            }
            install(ContentNegotiation) {
                jackson {
                    setSerializationInclusion(JsonInclude.Include.NON_NULL)
                    disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    registerModules(JavaTimeModule())
                }
            }

            defaultRequest {
                header("content-type", "application/json")
            }
        }
}
