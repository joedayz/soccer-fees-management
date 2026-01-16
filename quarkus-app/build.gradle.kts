plugins {
    id("io.quarkus")
    id("org.jetbrains.kotlin.jvm")
}

dependencies {
    implementation(enforcedPlatform("io.quarkus.platform:quarkus-bom:3.15.3"))
    implementation("io.quarkus:quarkus-resteasy-reactive-jackson")
    implementation("io.quarkus:quarkus-jdbc-postgresql")
    implementation("io.quarkus:quarkus-kotlin")
    implementation(project(":common"))
}

kotlin {
    jvmToolchain(17)
}
