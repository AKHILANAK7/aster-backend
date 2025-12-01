import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.0"
    application
}

val vertxVersion = "4.4.4"

repositories {
    mavenCentral()
}

dependencies {
    // Vert.x Core
    implementation(platform("io.vertx:vertx-stack-depchain:$vertxVersion"))
    implementation("io.vertx:vertx-lang-kotlin")
    implementation("io.vertx:vertx-lang-kotlin-coroutines")
    implementation("io.vertx:vertx-web")
    implementation("io.vertx:vertx-mongo-client")
    implementation("io.vertx:vertx-auth-jwt:4.4.4")
    implementation("io.vertx:vertx-auth-common:4.4.4")
    
    // Kotlin
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.2")
    
    // Logging
    implementation("org.slf4j:slf4j-api:2.0.7")
    implementation("ch.qos.logback:logback-classic:1.4.8")

    implementation("org.mindrot:jbcrypt:0.4")
    
    testImplementation(kotlin("test"))
}

application {
    mainClass.set("com.aster.MainVerticleKt")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs = listOf("-Xjsr305=strict")
    }
}

tasks.register<JavaExec>("runVertx") {
    mainClass.set("io.vertx.core.Launcher")
    classpath = sourceSets["main"].runtimeClasspath
    args = listOf("run", "com.aster.MainVerticle")
}

