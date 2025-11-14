/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.controller;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("/api/dashboard-data")
@RequiredArgsConstructor
public class DashboardController {

    @GetMapping
    public Map<String, String> dashboardData(@AuthenticationPrincipal OAuth2User principal) {
        debug("dashboardData", principal);
        if (principal == null) {
            throw new RuntimeException("Not authenticated");
        }

        return Map.of(
                "message", "Welcome to Dashboard, " + principal.getAttribute("name") + "!",
                "actuatorUrl", "http://localhost:8088/actuator",
                "swaggerUrl", "http://localhost:8088/swagger-ui/index.html",
                "h2ConsoleUrl", "http://localhost:8088/h2-console",
                "userEmail", principal.getAttribute("email"),
                "authenticationMethod", "OAuth2 Session"
        );
    }

    private void debug(String method, OAuth2User principal) {
        log.debug("method {} principal {}", method, principal);
        if (principal != null) {
            log.debug("method {} name {}", method, principal.getAttribute("name"));
            log.debug("method {} email {}", method, principal.getAttribute("email"));
            log.debug("method {} authorities {}", method, principal.getAuthorities().stream()
                    .map(auth -> auth.getAuthority())
                    .toList());
        }
    }
}


/*
/*
Key Features

RESTful API with HATEOAS:
    Resources include links to related resources
    Follows REST principles
    Self-descriptive messages

Web Interface with Spring MVC:
    Traditional server-side rendering
    Thymeleaf templates for HTML generation
    Simple CRUD operations through web forms

Data Model:
    JPA entities with proper relationships
    Repository pattern for data access

Separation of Concerns:
    API endpoints separate from web interface
    Clear distinction between data model and resource representation

This implementation provides a solid foundation that can be extended with additional features like:

    Authentication and authorization
    Validation
    Advanced search capabilities
    Pagination
    Caching
    API documentation with Swagger
*/
