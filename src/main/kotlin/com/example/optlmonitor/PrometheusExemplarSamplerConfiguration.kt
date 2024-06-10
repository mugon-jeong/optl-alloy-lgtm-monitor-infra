package com.example.optlmonitor

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Metrics
import io.prometheus.client.exemplars.tracer.otel_agent.OpenTelemetryAgentSpanContextSupplier
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*


@Configuration
class PrometheusExemplarSamplerConfiguration {
    @Bean
    fun openTelemetryAgentSpanContextSupplier(): OpenTelemetryAgentSpanContextSupplier {
        return OpenTelemetryAgentSpanContextSupplier()
    }

    @Bean
    @ConditionalOnClass(name = ["io.opentelemetry.javaagent.OpenTelemetryAgent"])
    fun otelRegistry(): MeterRegistry? {
        val otelRegistry: Optional<MeterRegistry> = Metrics.globalRegistry.registries.stream()
            .filter { r -> r.javaClass.getName().contains("OpenTelemetryMeterRegistry") }
            .findAny()
        otelRegistry.ifPresent(Metrics.globalRegistry::remove)
        return otelRegistry.orElse(null)
    }
}