/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.config;

import com.jvidia.aimlbda.service.aiml.OllamaService;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class OllamaConfig {
    public static final String MY_OLLAMA_MODEL = "MY_OLLAMA_MODEL";
    public static final String OLLAMA_CHAT_MODEL = "OLLAMA_CHAT_MODEL";

    @Value("${ollama.baseUrl}")
    private String ollamaBaseUrl;
    
    @Value("${ollama.modelName}")
    private String modelName;
    
    @Value("${ollama.timeout:60}")
    private int timeoutSeconds;

    @Primary
    @Bean
    public WebClient ollamaWebClient(OllamaProperties properties) {
        return WebClient.builder()
                .baseUrl(properties.getBaseUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Bean
    public OllamaService ollamaService(OllamaProperties properties, WebClient ollamaWebClient) {
        return new OllamaService(properties, ollamaWebClient);
    }

    @Bean(name = MY_OLLAMA_MODEL)
    public OllamaChatModel myOllamaChatModel() {
        return OllamaChatModel.builder()
                .baseUrl(ollamaBaseUrl)
                .modelName(modelName)
                .timeout(Duration.ofSeconds(timeoutSeconds))
                .logRequests(true)
                .logResponses(true)
                .build();
    }

    @Bean(name = OLLAMA_CHAT_MODEL)
    public ChatModel chatModel() {
        return dev.langchain4j.model.ollama.OllamaChatModel.builder()
                .baseUrl(ollamaBaseUrl)
                .modelName(modelName)
                //.httpClientBuilder(SpringRestClientBuilderFactory.INSTANCE)
                .timeout(Duration.ofSeconds(timeoutSeconds))
                .logRequests(true)
                .logResponses(true)
                .temperature(0.7)
                .topP(0.9)
                .build();
    }
}
