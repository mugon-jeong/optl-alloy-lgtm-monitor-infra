rootProject.name = "optl-monitor"

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            version("opentelemetry", "1.33.3")
            library("opentelemetry.api", "io.opentelemetry", "opentelemetry-api").versionRef("opentelemetry")
            library("opentelemetry.annotations", "io.opentelemetry", "opentelemetry-extension-annotations").versionRef("opentelemetry")
            library("opentelemetry.javaagent","io.opentelemetry.javaagent","opentelemetry-javaagent").versionRef("opentelemetry")
        }
    }
}