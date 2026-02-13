package com.sotti.product.configuration

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.r2")
data class R2Properties(
    val accessKey: String = "",
    val secretKey: String = "",
    val endpoint: String = "",
    val bucket: String = "",
    val publicBaseUrl: String = "",
)
