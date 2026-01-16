plugins {
    id("io.micronaut.application")
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.kapt")
}

micronaut {
    version("4.5.0")
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("com.soccerfees.micronaut.*")
    }
}

dependencies {
    implementation(project(":common"))
    implementation("io.micronaut.kotlin:micronaut-kotlin-runtime")
    implementation("io.micronaut:micronaut-http-server-netty")
    implementation("io.micronaut:micronaut-jackson-databind")
    implementation("io.micronaut.sql:micronaut-jdbc-hikari")
    implementation("org.postgresql:postgresql")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    kapt("io.micronaut:micronaut-inject-java")
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("com.soccerfees.micronaut.Application")
}
