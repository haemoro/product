import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile
import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.springframework.boot.gradle.plugin.SpringBootPlugin

plugins {
    id("org.springframework.boot") version "3.5.5"
    id("org.jlleitschuh.gradle.ktlint") version "12.2.0" apply false
    id("com.google.osdetector") version "1.7.3"
    kotlin("jvm") version "2.2.10"
    kotlin("plugin.spring") version "2.2.10"
}

group = "com.sotti"

allprojects {
    apply(plugin = "idea")
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    repositories {
        mavenCentral()
    }
}

object DependencyVersions {
    const val KOTLINX_COROUTINES = "1.10.2"
    const val KTOR_CLIENT = "3.2.3"
    const val SPRINGDOC_OPENAPI = "2.8.11"
    const val KOTEST = "5.9.1"
    const val SLACK_CLIENT = "1.45.3"
    const val AWS_SDK = "2.25.27"
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform(SpringBootPlugin.BOM_COORDINATES))
    implementation(platform("org.springframework.cloud:spring-cloud-dependencies:2025.0.0"))

    if (osdetector.classifier == "osx-aarch_64") {
        runtimeOnly("io.netty:netty-resolver-dns-native-macos:4.1.77.Final:${osdetector.classifier}")
    }

    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-web") {
        exclude("org.springframework.boot", "spring-boot-starter-tomcat")
    }
    implementation("org.springframework.boot:spring-boot-starter-undertow") {
        exclude("io.undertow", "undertow-websockets-jsr")
    }
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-validation")
//    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
//    implementation("org.springframework.boot:spring-boot-starter-data-jdbc") // JDBC 사용하지 않음

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.apache.commons:commons-pool2:2.12.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${DependencyVersions.KOTLINX_COROUTINES}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:${DependencyVersions.KOTLINX_COROUTINES}")

    implementation("io.ktor:ktor-client-core:${DependencyVersions.KTOR_CLIENT}")
    implementation("io.ktor:ktor-client-core-jvm:${DependencyVersions.KTOR_CLIENT}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:${DependencyVersions.KOTLINX_COROUTINES}")
    implementation("io.ktor:ktor-client-java:${DependencyVersions.KTOR_CLIENT}")
    implementation("io.ktor:ktor-client-content-negotiation:${DependencyVersions.KTOR_CLIENT}")
    implementation("io.ktor:ktor-serialization-jackson:${DependencyVersions.KTOR_CLIENT}")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:${DependencyVersions.SPRINGDOC_OPENAPI}")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-api:${DependencyVersions.SPRINGDOC_OPENAPI}")
    implementation("org.springdoc:springdoc-openapi-starter-common:${DependencyVersions.SPRINGDOC_OPENAPI}")

    implementation("com.slack.api:slack-api-model-kotlin-extension:${DependencyVersions.SLACK_CLIENT}")
    implementation("com.slack.api:slack-api-client:${DependencyVersions.SLACK_CLIENT}")
    implementation("com.github.ben-manes.caffeine:caffeine:3.2.0")

    implementation("org.apache.poi:poi-ooxml:5.4.0")

    implementation(platform("software.amazon.awssdk:bom:${DependencyVersions.AWS_SDK}"))
    implementation("software.amazon.awssdk:s3")

    implementation("org.sejda.imageio:webp-imageio:0.1.6")

    runtimeOnly("net.logstash.logback:logstash-logback-encoder:8.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.mockk:mockk:1.13.17")
    testImplementation("io.kotest:kotest-runner-junit5:${DependencyVersions.KOTEST}")
    testImplementation("io.kotest:kotest-assertions-core:${DependencyVersions.KOTEST}")
    testImplementation("io.kotest.extensions:kotest-extensions-spring:1.3.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:${DependencyVersions.KOTLINX_COROUTINES}")
}

tasks
    .withType<KotlinJvmCompile>()
    .configureEach {
        compilerOptions.jvmTarget.set(JvmTarget.JVM_21)
        compilerOptions.languageVersion.set(KotlinVersion.KOTLIN_2_1)
        compilerOptions.freeCompilerArgs.add("-Xjsr305=strict")
    }

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<org.jlleitschuh.gradle.ktlint.tasks.BaseKtLintCheckTask> {
    workerMaxHeapSize.set("512m")
}

configure<KtlintExtension> {
    version.set("1.5.0")
    enableExperimentalRules.set(true)
}
