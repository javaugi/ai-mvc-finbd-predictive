/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.test.aiml;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureWebTestClient
@Disabled("Temporarily disabled for CICD - enable to run test only when Ollama starts up")
public class OllamaConnectionTest {

    @Test
    public void testOllamaConnection() {
        WebClient testClient = WebClient.create("http://localhost:11434");

        testClient.get()
                .uri("/api/tags")
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(response -> System.out.println("Ollama models: " + response))
                .doOnError(error -> System.out.println("Ollama not available: " + error.getMessage()))
                .doOnSuccess(success -> System.out.println("Ollama not available: " + success))
                .subscribe();
    }
}
