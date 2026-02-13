package com.sotti.product.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.auditing.DateTimeProvider
import org.springframework.data.mongodb.MongoDatabaseFactory
import org.springframework.data.mongodb.MongoTransactionManager
import org.springframework.data.mongodb.config.EnableMongoAuditing
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.convert.DbRefResolver
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper
import org.springframework.data.mongodb.core.convert.MappingMongoConverter
import org.springframework.data.mongodb.core.convert.MongoConverter
import org.springframework.data.mongodb.core.mapping.MongoMappingContext
import java.time.LocalDateTime
import java.util.Optional
import java.util.concurrent.TimeUnit

@Configuration
@EnableMongoAuditing(dateTimeProviderRef = "auditingDateTimeProvider")
class MongoConfiguration(
    private val objectMapper: ObjectMapper,
) {
    @Bean
    fun connectionPoolCustomizer(): MongoClientSettingsBuilderCustomizer =
        MongoClientSettingsBuilderCustomizer { builder ->
            builder.applyToConnectionPoolSettings { pool ->
                pool
                    .minSize(10)
                    .maxSize(30)
                    .maxWaitTime(10, TimeUnit.SECONDS)
                    .maxConnectionIdleTime(5, TimeUnit.MINUTES)
                    .maxConnectionLifeTime(30, TimeUnit.MINUTES)
            }
        }

    @Bean
    @Primary
    fun transactionManager(dbFactory: MongoDatabaseFactory): MongoTransactionManager = MongoTransactionManager(dbFactory)

    @Bean
    fun mappingMongoConverter(
        mongoDatabaseFactory: MongoDatabaseFactory,
        mongoMappingContext: MongoMappingContext,
    ): MappingMongoConverter {
        val dbRefResolver: DbRefResolver = DefaultDbRefResolver(mongoDatabaseFactory)
        val converter = MappingMongoConverter(dbRefResolver, mongoMappingContext)
        // 이 설정을 해줘야 _class 타입이 저장안 됨
        converter.setTypeMapper(DefaultMongoTypeMapper(null))

        return converter
    }

    @Bean
    @Primary
    fun mongoTemplate(
        dbFactory: MongoDatabaseFactory,
        converter: MongoConverter,
    ) = MongoTemplate(dbFactory, converter)

    @Bean(name = ["auditingDateTimeProvider"])
    fun dateTimeProvider(): DateTimeProvider = DateTimeProvider { Optional.of(LocalDateTime.now()) }
}
