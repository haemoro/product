# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Kids music quiz API (소띠뮤직) built with Kotlin + Spring Boot 3.5.5. MongoDB-backed REST API serving music quiz data for a children's game application.

## Build & Development Commands

```bash
./gradlew bootRun                    # Run application locally
./gradlew clean bootJar              # Build deployable JAR
./gradlew test                       # Run all tests (JUnit 5 / Kotest)
./gradlew ktlintCheck                # Check code style
./gradlew ktlintFormat               # Auto-format code
./gradlew test --tests "ClassName"   # Run a single test class
```

KtLint runs with experimental rules enabled (v1.5.0). Always run `ktlintCheck` before committing.

## Tech Stack

- **Language**: Kotlin 2.2.10 (JVM 21)
- **Framework**: Spring Boot 3.5.5 with Undertow (not Tomcat)
- **Database**: MongoDB (Spring Data MongoDB with transaction support)
- **HTTP Client**: Ktor Client 3.2.3 (Java engine, Jackson serialization)
- **API Docs**: SpringDoc OpenAPI 2.8.11 (Swagger UI at `/swagger-ui.html`)
- **Testing**: Kotest 5.9.1 + MockK 1.13.17
- **Linting**: KtLint 1.5.0 via Gradle plugin

## Architecture

Standard layered Spring Boot architecture under `com.sotti.product`:

```
controller/  -> REST endpoints (annotated with Swagger @Operation)
service/     -> Business logic
repository/  -> Spring Data MongoDB repositories
domain/      -> MongoDB document entities + enums
dto/         -> Request/Response DTOs (separate from domain)
configuration/ -> Spring @Configuration classes
```

Single domain aggregate: **MusicQuiz** with categories (NURSERY_RHYME, ANIMATION_OST, DISNEY, KIDS_POP, EDUCATION, CHARACTER_SONG).

API base path: `/api/v1/music-quiz`

Key design decisions:
- Game endpoints hide the answer field (separate `MusicQuizGameResponse` DTO)
- Answer checking is case-insensitive with whitespace trimming
- MongoDB configured with custom `DefaultMongoTypeMapper(null)` to omit `_class` field
- CSRF disabled and all endpoints are public (no auth layer)

## Configuration

- `application.yaml` - Base config, MongoDB URI from `MONGODB_URI` env var
- `application-local.yaml` - Local development profile
- `application-production.yaml` - Production profile with restricted actuator endpoints

Required environment variables:
- `MONGODB_URI` - MongoDB connection string
- `SPRING_PROFILES_ACTIVE` - Profile selection (local/production)

## Deployment

Docker multi-stage build (Gradle 8.14.3 + JDK 21 build, JRE 21 slim runtime). Deployed to Koyeb with auto-deploy from GitHub. See `KOYEB_DEPLOY.md` for details.
