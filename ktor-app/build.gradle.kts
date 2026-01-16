plugins {
    id("org.jetbrains.kotlin.jvm")
    id("application")
}

dependencies {
    implementation(project(":common"))
    implementation("io.ktor:ktor-server-core-jvm:2.3.12")
    implementation("io.ktor:ktor-server-netty-jvm:2.3.12")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:2.3.12")
    implementation("io.ktor:ktor-serialization-jackson-jvm:2.3.12")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.2")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.17.2")
    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("org.postgresql:postgresql:42.7.4")
    implementation("org.slf4j:slf4j-simple:2.0.13")
}

application {
    mainClass.set("com.soccerfees.ktor.ApplicationKt")
}

kotlin {
    jvmToolchain(17)
}
