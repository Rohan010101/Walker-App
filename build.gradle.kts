plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.plugin.serialization)
}

group = "com.example"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

dependencies {
    implementation(libs.ktor.server.metrics)
    implementation(libs.ktor.server.compression)
    implementation(libs.ktor.server.default.headers)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.sessions)
    implementation(libs.ktor.server.host.common)
    implementation(libs.ktor.server.status.pages)
    implementation(libs.ktor.server.call.logging)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.server.websockets)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.ktor.server.netty)
    implementation(libs.logback.classic)
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)

    implementation(libs.kmongo)
    implementation(libs.kmongo.coroutine)
    implementation(libs.commons.codec)


    configurations.create("sshAntTask")
    "sshAntTask"(libs.ant.jsch)

    implementation("com.auth0:java-jwt:4.2.1")
    implementation("org.mindrot:jbcrypt:0.4")

    implementation("aws.sdk.kotlin:dynamodb:1.4.96")
    implementation("aws.sdk.kotlin:s3:1.4.96")
    implementation("aws.smithy.kotlin:http-client-engine-ktor-jvm:0.7.8")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
    implementation("io.ktor:ktor-client-core:2.3.7")
    implementation("io.ktor:ktor-client-cio:2.3.7") // 👈 This line fixes the CIO issue

    implementation("org.slf4j:slf4j-simple:1.7.30")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.17.2")
    implementation("org.apache.logging.log4j:log4j-core:2.17.2")
    implementation("org.apache.logging.log4j:log4j-api:2.17.2")


    // Exposed core
    implementation("org.jetbrains.exposed:exposed-core:0.55.0")
    // Exposed DAO
    implementation("org.jetbrains.exposed:exposed-dao:0.55.0")
    // Exposed JDBC
    implementation("org.jetbrains.exposed:exposed-jdbc:0.55.0")
    // Optional: for Kotlin datetime support
    implementation("org.jetbrains.exposed:exposed-java-time:0.55.0")


    implementation("io.projectreactor:reactor-core:3.6.4") // latest stable as of 2025


    // Logging

    // Ktor client
    implementation("io.ktor:ktor-client-logging:2.3.12")
    implementation("io.ktor:ktor-client-encoding:2.3.12") // gzip/deflate

    implementation("io.ktor:ktor-client-content-negotiation:2.3.12")


}
