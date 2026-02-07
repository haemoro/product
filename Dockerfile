# Build stage
FROM gradle:8.14.3-jdk21 AS build
WORKDIR /app
COPY build.gradle.kts settings.gradle.kts ./
COPY src ./src
RUN gradle clean bootJar --no-daemon

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget -qO- http://localhost:8080/actuator/health || exit 1

CMD ["java", \
  "-Xmx384m", \
  "-Xms256m", \
  "-XX:+UseG1GC", \
  "-XX:MaxMetaspaceSize=96m", \
  "-jar", "app.jar"]
