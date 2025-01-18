package com.example.demo.controllers;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Slf4j
public class ExampleController {

    private final Counter requestCounter;
    private final OpenTelemetry openTelemetry;

    public ExampleController(MeterRegistry registry, OpenTelemetry openTelemetry) {
        this.requestCounter = Counter.builder("api.requests.total")
                .description("Total API requests")
                .register(registry);
        this.openTelemetry = openTelemetry;
    }

    @GetMapping("/example")
    public ResponseEntity<String> example() {
        // Metrics
        requestCounter.increment();

        // Tracing
        Tracer tracer = openTelemetry.getTracer(ExampleController.class.getName());
        Span span = tracer.spanBuilder("example-operation").startSpan();

        try (Scope scope = span.makeCurrent()) {
            // Logging (will automatically include trace ID from current span)
            log.info("Processing example request");

            // Your business logic here

            return ResponseEntity.ok("Success");
        } finally {
            span.end();
        }
    }
}

