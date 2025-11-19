/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.controller.aiml;

import static com.jvidia.aimlbda.config.RestTemplateConfig.OPEN_AI_CHAT_MODEL_DS;
import com.jvidia.aimlbda.dto.aiml.OllamaRequest;
import com.jvidia.aimlbda.service.aiml.OpenAiApiDsService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/openaids")
public class OpenAiDsController {

    private static final Logger log = LoggerFactory.getLogger(OpenAiDsController.class);
    
    private final @Qualifier(OPEN_AI_CHAT_MODEL_DS)
    OpenAiChatModel chatModel;
    
    private final OpenAiApiDsService dsService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<String>> queryOllamaByWebClient(@RequestBody OllamaRequest ollamaRequest) {
        log.debug("queryOllamaByWebClient {}", ollamaRequest);
        return dsService.queryOpenAiDsByWebClient(ollamaRequest)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build())
            .onErrorResume(Exception.class, ex -> {
                log.error("queryOllamaByWebClient ", ex);
            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
            });
    }

    @PostMapping(value = "/stream", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<ResponseEntity<String>> streamOllamaByWebClient(@RequestBody OllamaRequest ollamaRequest) {
        log.debug("queryOllamaByWebClient {}", ollamaRequest);
        return dsService.streamOpenAiDsByWebClient(ollamaRequest)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build())
            .onErrorResume(Exception.class, ex -> {
                log.error("streamOllamaByWebClient ", ex);
            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
            });
    }

    //Send a POST request (e.g., using curl or Postman):
    //curl -X POST http://localhost:8080/api/deepseek/chat \
    // -H "Content-Type: text/plain" \
    // -d "Explain quantum computing in simple terms." 

    @PostMapping("/chat") //this one does not seem to work
    public Mono<String> chat(@RequestBody String userPrompt) {
        //userPrompt = ML_Q;
        return dsService.getChatResponse(userPrompt);
    }    

    //- this works though
    @GetMapping(value = "/test", produces = MediaType.APPLICATION_JSON_VALUE) //http://localhost:8080/api/deepseek/test 
    public Mono<String> chatTest(@RequestParam(value = "userPrompt", defaultValue = "Tell me a joke") String userPrompt) {
        //userPrompt = ML_Q;
        log.debug("chatTest userPrompt {}", userPrompt);
        return dsService.getChatResponse(userPrompt);
    }    

    //- this works though
    @GetMapping(value = "/test2", produces = MediaType.APPLICATION_JSON_VALUE) //http://localhost:8080/api/deepseek/test2
    public Mono<String> chatTest2(@RequestParam(value = "userPrompt", defaultValue = "Tell me a joke") String userPrompt) {
        //userPrompt = ML_Q;
        log.debug("chatTest2 userPrompt {}", userPrompt);
        return dsService.getAIResponse(userPrompt);
    }    
 
    //- this DOES NOT works though
    @GetMapping("/chatgen")
    public Map generate(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        //message = ML_Q;
        return Map.of("generation", this.chatModel.call(message));
    }

    //- this DOES NOT works though
    @GetMapping(value = "/chatstream", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<ChatResponse> generateStream(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        //message = ML_Q;
        Prompt prompt = new Prompt(new UserMessage(message));
        return this.chatModel.stream(prompt);
    }    
    
    @GetMapping(value = "/prompt", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<ChatResponse> generateResponse(String prompt) {
        return this.chatModel.stream(
            new Prompt(prompt,
                OpenAiChatOptions.builder()
                    .model("deepseek-chat")
                    .temperature(0.4)
                    .build()
            ));
    }

}
