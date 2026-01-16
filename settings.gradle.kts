pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
    plugins {
        id("org.jetbrains.kotlin.jvm") version "1.9.24"
        id("org.jetbrains.kotlin.plugin.serialization") version "1.9.24"
        id("org.jetbrains.kotlin.plugin.spring") version "1.9.24"
        id("org.jetbrains.kotlin.kapt") version "1.9.24"
        id("org.springframework.boot") version "3.2.5"
        id("io.spring.dependency-management") version "1.1.5"
        id("io.micronaut.application") version "4.5.0"
        id("io.micronaut.aot") version "4.5.0"
        id("io.quarkus") version "3.15.3"
    }
}

rootProject.name = "soccer-fees-management"
include("common", "ktor-app", "spring-app", "micronaut-app", "quarkus-app")
