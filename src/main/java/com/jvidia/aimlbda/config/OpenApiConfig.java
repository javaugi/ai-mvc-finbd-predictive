package com.jvidia.aimlbda.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
//Global API Configuration (Optional but Recommended)
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AI Fintech Predictive Analysis Documentation")
                        .version("v1.0")
                        .description("""
                            This is the API documentation for the AI Fintech Predictive Analysis System.
                            It provides endpoints for managing orders, fulfillment processes, and related projects.
                            **Important:** All endpoints require JWT authentication.
                            """)
                        .termsOfService("http://swagger.io/terms/")
                        .contact(new Contact()
                                .name("AI Fintech Predictive Analysis Development Team")
                                .email("support@company.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .externalDocs(new ExternalDocumentation()
                        .description("More about My API")
                        .url("https://github.com/myorg/myapi"))
                .servers(List.of(
                        new Server().url("http://localhost:3000").description("Frontend React Server - Dashboard"),
                        new Server().url("http://localhost:5173").description("Frontend React Server - Vite"),
                        new Server().url("http://localhost:8088").description("Development Server"),
                        new Server().url("https://api.myapp.com").description("Production Server")
                ))
                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"));
        // You can also add global security schemes here (e.g., JWT)
        // .components(new Components().addSecuritySchemes("bearer-jwt", ...))
        // .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"));
    }
}

/*
Spring Boot Actuator endpoints are JSON-only by design. They’re meant for monitoring systems (Prometheus, Grafana, ELK, etc.) rather than human-friendly HTML. That’s why when you hit:

you only see JSON.

✅ Ways to get HTML-like actuator info
1. Use Spring Boot Admin (recommended)

Add Spring Boot Admin — it provides a nice web UI over actuator.

Add dependency (client side, in your app):

<dependency>
    <groupId>de.codecentric</groupId>
    <artifactId>spring-boot-admin-starter-client</artifactId>
    <version>3.2.3</version> <!-- match your Spring Boot version -->
</dependency>


Add dependency (server side, separate app):

<dependency>
    <groupId>de.codecentric</groupId>
    <artifactId>spring-boot-admin-starter-server</artifactId>
    <version>3.2.3</version>
</dependency>


Then create a Spring Boot Admin server app:

@EnableAdminServer
@SpringBootApplication
public class AdminServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdminServerApplication.class, args);
    }
}


👉 Now you can open a dashboard in HTML that shows health, metrics, env, logs, etc.

2. Use Swagger/OpenAPI UI for documentation, not Actuator

    If you’re mainly after human-friendly documentation of your endpoints, that’s what Swagger UI (springdoc) is for.
    Actuator stays JSON, Swagger gives HTML UI.

3. Write a thin HTML wrapper (quick hack)

If you just want to pretty-print actuator info in HTML inside your app, you could add a controller like this:

@RestController
public class ActuatorUiController {

    private final WebClient webClient = WebClient.create("http://localhost:8080");

    @GetMapping(value = "/actuator-ui", produces = MediaType.TEXT_HTML_VALUE)
    public Mono<String> actuatorUi() {
        return webClient.get()
                .uri("/actuator/health")
                .retrieve()
                .bodyToMono(Map.class)
                .map(body -> "<html><body><h1>Health</h1><pre>" + body + "</pre></body></html>");
    }
}


Not as powerful as Spring Boot Admin, but works if you just need HTML output.

✅ Bottom line:
    JSON only → built-in actuator
    HTML UI → use Spring Boot Admin (best choice)
    Quick preview → custom controller hack
 */
