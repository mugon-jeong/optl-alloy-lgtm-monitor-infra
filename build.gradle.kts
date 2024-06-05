import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("org.springframework.boot") version "3.3.0"
    id("io.spring.dependency-management") version "1.1.5"
    kotlin("jvm") version "1.9.24"
    kotlin("plugin.spring") version "1.9.24"
    id("com.google.cloud.tools.jib") version "3.4.3"
    id("com.ryandens.javaagent-jib") version "0.5.1" // jvmflags에 자동으로 java agent 추가
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
}

dependencies {
    javaagent(libs.opentelemetry.javaagent)
    implementation("io.github.oshai:kotlin-logging-jvm:6.0.9")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
        jvmTarget = JvmTarget.JVM_21
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

jib {
    from {
        image = "amazoncorretto:21-alpine"
        platforms {
            platform {
                architecture = "arm64"
                os = "linux"
            }
        }
    }
    to {
        image = "optl-monitor"
        tags = setOf("${project.version}", "latest")
    }
    container {
        creationTime = "USE_CURRENT_TIMESTAMP"
        jvmFlags =
            listOf(
                "-Xms1024m",
                "-Xmx1024m",
                "-Dotel.metric.export.interval=500",
                "-Dotel.bsp.schedule.delay=500",
                "-Dotel.service.name=optl-service",
                "-Dotel.exporter.otlp.protocol=grpc",
                "-Dotel.exporter.otlp.endpoint=http://alloy:4317",
                "-Dotel.logs.exporter=otlp"
            )
        setAllowInsecureRegistries(true)
    }
}