/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.service.aiml;

import com.jvidia.aimlbda.config.OllamaProperties;
import com.jvidia.aimlbda.dto.aiml.OllamaChatRequest;
import com.jvidia.aimlbda.dto.aiml.OllamaChatResponse;
import com.jvidia.aimlbda.dto.aiml.OllamaEmbeddingResponse;
import com.jvidia.aimlbda.dto.aiml.OllamaMessage;
import com.jvidia.aimlbda.dto.aiml.OllamaRequest;
import com.jvidia.aimlbda.utils.EmbeddingUtil;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.embedding.EmbeddingRequest;
import com.theokanning.openai.embedding.EmbeddingResult;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class OllamaService {

    private final OllamaProperties properties;
    private final WebClient webClient;

    // Create similar to OpenAiService methods
    public Mono<EmbeddingResult> createEmbeddings(EmbeddingRequest request) {
        OllamaRequest ollamaRequest = new OllamaRequest(
            properties.getEmbeddingModel(),
            String.join(", ", request.getInput())
        );

        return webClient.post()
            .uri(properties.getApiUrl())
            .bodyValue(ollamaRequest)
            .retrieve()
            .bodyToMono(OllamaEmbeddingResponse.class)
            .map(response -> {
                EmbeddingResult result = new EmbeddingResult();
                result.setModel(properties.getEmbeddingModel());
            result.setData(EmbeddingUtil.convertDoublesToEmbeddings(response.getEmbedding()));
                return result;
            });
    }

    public Mono<ChatCompletionResult> createChatCompletion(ChatCompletionRequest request) {
        OllamaChatRequest ollamaRequest = new OllamaChatRequest(
            properties.getModel(),
            request.getMessages().stream()
                .map(m -> new OllamaMessage(m.getRole(), m.getContent()))
                .collect(Collectors.toList()),
            properties.getTemperature()
        );

        return webClient.post()
            .uri(properties.getApiUrl())
            .bodyValue(ollamaRequest)
            .retrieve()
            .bodyToMono(OllamaChatResponse.class)
            .map(response -> {
                ChatCompletionResult result = new ChatCompletionResult();
                result.setModel(properties.getModel());
            result.setChoices(EmbeddingUtil.convertToChatChoices(response));
                return result;
            });
    }

}
