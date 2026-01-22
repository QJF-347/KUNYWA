plugins {
    application
    kotlin("jvm") version "1.9.10"
    kotlin("plugin.serialization") version "1.9.10"
}

group = "com.supermarket"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core:2.3.6")
    implementation("io.ktor:ktor-server-netty:2.3.6")
    implementation("io.ktor:ktor-server-content-negotiation:2.3.6")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.6")
    implementation("io.ktor:ktor-server-status-pages:2.3.6")
    implementation("io.ktor:ktor-server-call-logging:2.3.6")
    implementation("io.ktor:ktor-server-cors:2.3.6")
    
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    
    // Database
    implementation("org.postgresql:postgresql:42.7.1")
    implementation("com.h2database:h2:2.2.224") // Keep for local testing
    
    // HTTP Client for M-Pesa
    implementation("io.ktor:ktor-client-core:2.3.6")
    implementation("io.ktor:ktor-client-cio:2.3.6")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.6")
    
    // Logging
    implementation("ch.qos.logback:logback-classic:1.4.11")
    
    // Password hashing
    implementation("org.mindrot:jbcrypt:0.4")
    
    testImplementation("io.ktor:ktor-server-tests:2.3.6")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.9.10")
}

application {
    mainClass.set("com.supermarket.ApplicationKt")
}

kotlin {
    jvmToolchain(8)
}
