import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.gradle.api.tasks.Copy

plugins {
    id("org.springframework.boot") version "3.3.0"
    id("io.spring.dependency-management") version "1.1.5"
    kotlin("jvm") version "1.9.24"
    kotlin("plugin.spring") version "1.9.24"
    id("com.google.cloud.tools.jib") version "3.4.3"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
}

tasks.withType<JavaExec> {
    jvmArgs("--add-exports", "jdk.management.agent/jdk.internal.agent.resources=ALL-UNNAMED")
}

configurations {
    create("agent")
}

dependencies {
    "agent"("io.opentelemetry.javaagent:opentelemetry-javaagent:2.4.0")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.opentelemetry:opentelemetry-api")
    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("io.micrometer:micrometer-tracing-bridge-brave") {
        // 프로젝트에서 Zipkin을 사용하지 않는 경우
        exclude(group = "io.zipkin.reporter2")
    }

    implementation("io.github.oshai:kotlin-logging-jvm:6.0.9")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    runtimeOnly("org.postgresql:postgresql")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dependencyManagement {
    imports {
        mavenBom("io.opentelemetry:opentelemetry-bom:1.39.0")
        mavenBom("io.opentelemetry:opentelemetry-bom-alpha:1.39.0-alpha")
        mavenBom("io.opentelemetry.instrumentation:opentelemetry-instrumentation-bom:2.4.0")
    }
}


tasks.register<Copy>("copyAgent") {
    from(configurations.getByName("agent")) {
        rename("opentelemetry-javaagent-.*\\.jar", "opentelemetry-javaagent.jar")
    }
    into(layout.projectDirectory.dir("src/main/jib/agent"))
}

tasks.named<Jar>("bootJar") {
    dependsOn(tasks.named("copyAgent"))
    archiveFileName.set("app.jar")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
        jvmTarget = JvmTarget.JVM_21
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    jvmArgs("--add-exports", "jdk.management.agent/jdk.internal.agent.resources=ALL-UNNAMED")
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
                "-javaagent:/agent/opentelemetry-javaagent.jar"
            )
        setAllowInsecureRegistries(true)
    }
}
