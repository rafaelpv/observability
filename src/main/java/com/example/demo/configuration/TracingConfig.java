package com.example.demo.configuration;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TracingConfig {

    private static final String SERVICE_NAME = "spring-boot-app";

    @Bean
    public OpenTelemetry openTelemetry() {
        // Configura o exportador OTLP
        OtlpGrpcSpanExporter spanExporter = OtlpGrpcSpanExporter.builder()
                .setEndpoint("http://tempo:4317") // Endpoint do OTLP (ajuste conforme necessário)
                .build();

        // Configura o BatchSpanProcessor
        BatchSpanProcessor spanProcessor = BatchSpanProcessor.builder(spanExporter).build();

        // Configura o SdkTracerProvider com os atributos do serviço
        SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
                .addSpanProcessor(spanProcessor)
                .setResource(Resource.getDefault().toBuilder()
                        .put("service.name", SERVICE_NAME) // Nome do serviço
                        .build())
                .build();

        // Cria e retorna o OpenTelemetry SDK
        return OpenTelemetrySdk.builder()
                .setTracerProvider(tracerProvider)
                .build();
    }

    @Bean
    public Tracer tracer(OpenTelemetry openTelemetry) {
        // Retorna um Tracer para o serviço
        return openTelemetry.getTracer(SERVICE_NAME);
    }
}
