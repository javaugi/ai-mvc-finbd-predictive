/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.config;

import com.jvidia.aimlbda.service.aiml.OllamaService;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@lombok.extern.slf4j.Slf4j
@Configuration
public class OllamaConfig {
    public static final String MY_OLLAMA_MODEL = "MY_OLLAMA_MODEL";
    public static final String OLLAMA_CHAT_MODEL = "OLLAMA_CHAT_MODEL";

    @Autowired
    protected OllamaProperties ollamaProps;

    @PostConstruct
    public void init() {
        log.debug("OllamaConfig {}", ollamaProps);
    }

    @Primary
    @Bean
    public WebClient ollamaWebClient() {
        return WebClient.builder()
                .baseUrl(ollamaProps.getBaseUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Bean
    public OllamaService ollamaService() {
        return new OllamaService(ollamaProps, ollamaWebClient());
    }

    @Bean(name = MY_OLLAMA_MODEL)
    public OllamaChatModel myOllamaChatModel() {
        return OllamaChatModel.builder()
                .baseUrl(ollamaProps.getBaseUrl())
                .modelName(ollamaProps.getModel())
                .timeout(Duration.ofSeconds(ollamaProps.getConnTimeoutMillis()))
                .logRequests(true)
                .logResponses(true)
                .build();
    }

    @Bean(name = OLLAMA_CHAT_MODEL)
    public ChatModel chatModel() {
        return dev.langchain4j.model.ollama.OllamaChatModel.builder()
                .baseUrl(ollamaProps.getBaseUrl())
                .modelName(ollamaProps.getModel())
                //.httpClientBuilder(SpringRestClientBuilderFactory.INSTANCE)
                .timeout(Duration.ofSeconds(ollamaProps.getConnTimeoutMillis()))
                .logRequests(true)
                .logResponses(true)
                .temperature(0.7)
                .topP(0.9)
                .build();
    }
}
