/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.actuator.openapi;

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
public class OpenApiConfig {

    /*
src/
├── main/
│   ├── java/
│   │   └── com/example/
│   │       ├── config/
│   │       │   └── OpenApiConfig.java
│   │       ├── controller/
│   │       │   └── UserController.java
│   │       ├── dto/
│   │       │   └── UserDto.java
│   │       └── Application.java
│   └── resources/
│       └── application.yml
pom.xml    
     */

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("My API Documentation")
                        .version("1.0")
                        .description("""
						This is the API documentation for the Next-Gen Software.
						It provides endpoints for managing orders, fulfillment processes, and related projects.
						**Important:** All endpoints require JWT authentication.
						""")
                        .termsOfService("http://swagger.io/terms/")
                        .contact(new Contact().name("The Next-Gen Development Team").email("support@nextgen.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")))
                .externalDocs(new ExternalDocumentation()
                        .description("More about My API")
                        .url("https://github.com/myorg/myapi"))
                .servers(List.of(
                        new Server().url("http://localhost:8088").description("Development Server"),
                        new Server().url("https://api.myapp.com").description("Production Server")
                )).addSecurityItem(new SecurityRequirement().addList("bearer-jwt"));
        // You can also add global security schemes here (e.g., JWT)
        // .components(new Components().addSecuritySchemes("bearer-jwt", ...))
        // .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"));
    }
}
