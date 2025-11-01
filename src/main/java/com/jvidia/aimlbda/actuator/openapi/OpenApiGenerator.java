/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.actuator.openapi;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.models.OpenAPI;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class OpenApiGenerator {

    @Autowired
    private OpenAPI openAPI;

    @EventListener(ApplicationReadyEvent.class)
    public void generateOpenApiSpec() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        String openApiJson = mapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(openAPI);

        Files.writeString(
                Paths.get("target/openapi.json"),
                openApiJson,
                StandardOpenOption.CREATE
        );

        System.out.println("OpenAPI spec generated at: target/openapi.json");
    }
}
