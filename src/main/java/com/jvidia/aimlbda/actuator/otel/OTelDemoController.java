/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.actuator.otel;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import java.util.HashMap;
import java.util.Map;
//import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/otelapi")
public class OTelDemoController {
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("demo-controller");

    @GetMapping("/hello/{name}")
    public Map<String, String> hello(@PathVariable String name) {
        // Create a custom span
        Span span = tracer.spanBuilder("hello-operation").startSpan();

        try (Scope scope = span.makeCurrent()) {
            // Add attributes to the span
            span.setAttribute("user.name", name);
            span.setAttribute("http.method", "GET");

            // Simulate some work
            String message = processGreeting(name);

            Map<String, String> response = new HashMap<>();
            response.put("message", message);
            response.put("status", "success");

            return response;

        } catch (Exception e) {
            span.recordException(e);
            throw e;
        } finally {
            span.end();
        }
    }

    private String processGreeting(String name) {
        // Create another nested span
        Span innerSpan = tracer.spanBuilder("process-greeting").startSpan();

        try (Scope scope = innerSpan.makeCurrent()) {
            // Simulate processing time
            Thread.sleep(100);
            return "Hello, " + name + "!";
        } catch (InterruptedException e) {
            innerSpan.recordException(e);
            Thread.currentThread().interrupt();
            return "Error processing greeting";
        } finally {
            innerSpan.end();
        }
    }

    @GetMapping("/users")
    public Map<String, Object> getUsers() {
        // Get the current span (automatically created by Spring Boot)
        Span currentSpan = Span.current();
        currentSpan.setAttribute("endpoint", "/otelapi/users");
        currentSpan.addEvent("Fetching users list");

        Map<String, Object> response = new HashMap<>();
        response.put("users", java.util.List.of("Alice", "Bob", "Charlie"));
        response.put("count", 3);

        currentSpan.addEvent("Users fetched successfully");

        return response;
    }
}
