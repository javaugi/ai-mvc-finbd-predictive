/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.test.aiml;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;

/*
FINAL RULES TO FOLLOW
You Use                 Allowed Test Client             Allowed Auth Mock
MVC (Servlet)           MockMvc                         oauth2Login(), oidcLogin(), user(), jwt()
MVC + WebTestClient     WebTestClient (Servlet mode)	mockJwt() only
WebFlux App             WebTestClient                   mockOAuth2Login()

✅ Final Correct Test (use this)
✔ If you're using MockMvc:
mockMvc.perform(
        post("/api/gemini")
            .with(oauth2Login()) // or oidcLogin()
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                    "prompt": "Explain quantum computing"
                }
            """)
    )
    .andExpect(status().isOk());

✔ If using WebTestClient:
webTestClient
    .mutateWith(mockJwt())
    .post()
    .uri("/api/gemini")
    .contentType(MediaType.APPLICATION_JSON)
    .bodyValue(Map.of("prompt", "Explain quantum computing"))
    .exchange()
    .expectStatus().isOk();

The example code:
    mockMvc.perform(post("/api/gemini").contentType(MediaType.APPLICATION_JSON).content(content).with(oauth2Login())
    mockMvc.perform(post("/api/gemini").contentType(MediaType.APPLICATION_JSON).content(content).with(oidcLogin())
    webTestClient.mutateWith(mockJwt().authorities(() -> "ROLE_USER"))

Issue                                                   Fix
App requires JWT                                        mutateWith(mockJwt())
App uses OAuth login                                    mutateWith(mockOAuth2Login())
App uses custom roles                                   Ensure mockJwt().authorities("SCOPE_xyz")
Tests are @WebFluxTest and missing security context     Add TestSecurityConfig
Reactive app + spring-security-test missing             Add dependency

(1) If your controller uses @RequestParam like 
        @PostMapping("/api/gemini")
        public Mono<?> queryByWebClient(@RequestParam String prompt)
    You should user param
        mockMvc.perform(
                post("/api/gemini")
                    .with(oauth2Login())
                    .param("prompt", "Explain quantum computing")
            )
            .andExpect(status().isOk());
(2) If your controller uses @RequestBody like 
        @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
        public Mono<ResponseEntity<String>> queryOllamaByWebClient(@RequestBody OllamaRequest ollamaRequest)
    You should user param
        webTestClient
            .mutateWith(mockJwt())
            .post()
            .uri("/api/gemini")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(Map.of("prompt", "Explain quantum computing"))
            .exchange()
            .expectStatus().isOk();

    // Create a request body as a Map, which will be serialized to JSON
     String content = """
                 {
                     "prompt": "Explain quantum computing"
                 }
             """;
    mockMvc.perform(post("/api/gemini")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .with(oauth2Login()) // <— correct for MVC

        webTestClient.mutateWith(mockJwt().authorities(() -> "ROLE_USER")) // <-- works for MVC
                .mutate()
                .responseTimeout(Duration.ofSeconds(customTimeout)) // Increase timeout
                .build()
                .post()
                .uri(uriBuilder -> uriBuilder
                .path("/api/ollama")
                .queryParam("prompt", "Explain quantum computing")
                .build()
                )
                .uri("/api/ollama")
                .contentType(MediaType.APPLICATION_JSON)

        webTestClient.mutateWith(mockJwt().authorities(() -> "ROLE_USER")) // <-- works for MVC
                .mutate()
                .responseTimeout(Duration.ofSeconds(customTimeout)) // Increase timeout
                .build()
                .post()
                .uri("/api/ollama")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("prompt", "Explain quantum computing"))
 */
@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
@Disabled("Temporarily disabled for CICD - all tests pass without issues but time-consuming")
public class GeminiApiTest {

    long customTimeout = 60000L; //1 minute

    // Create a request body as a Map, which will be serialized to JSON
    String content = """
                {
                    "prompt": "Explain quantum computing briefly"
                }
            """;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void testByWebClientWithOAuth() throws Exception {
        MvcResult asyncResult = mockMvc.perform(post("/api/gemini")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .with(oauth2Login()) // <— correct for MVC
        ).andDo(print())
                .andExpect(request().asyncStarted())
                .andReturn();

        // Set the timeout for the async context (optional, default is usually 30s)
        // This must be done *after* the initial request returns but *before* the async result is ready
        asyncResult.getRequest().getAsyncContext().setTimeout(customTimeout); // 10 seconds

        // Wait for the async result and perform subsequent assertions via asyncDispatch
        mockMvc.perform(asyncDispatch(asyncResult))
                .andExpect(status().isOk()) // Now assert the final status code
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)) // Assert content type
                .andExpect(content().string(containsString("quantum"))); // Assert content body

        if (asyncResult.getResponse() != null) {
            String responseBody = asyncResult.getResponse().getContentAsString();
            System.out.println("testByWebClientWithOAuth /api/gemini \n Response: " + responseBody);
        }
        assertTrue(asyncResult.getResponse() != null && asyncResult.getResponse().getStatus() == 200);
    }

    @Test
    public void testByWebClientWithOid() throws Exception {
        MvcResult asyncResult = mockMvc.perform(post("/api/gemini")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .with(oidcLogin()) // <— correct for MVC
        ).andDo(print())
                .andExpect(request().asyncStarted())
                .andReturn();

        // Set the timeout for the async context (optional, default is usually 30s)
        // This must be done *after* the initial request returns but *before* the async result is ready
        asyncResult.getRequest().getAsyncContext().setTimeout(customTimeout); // 10 seconds

        // Wait for the async result and perform subsequent assertions via asyncDispatch
        mockMvc.perform(asyncDispatch(asyncResult))
                .andExpect(status().isOk()) // Now assert the final status code
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)) // Assert content type
                .andExpect(content().string(containsString("quantum"))); // Assert content body

        if (asyncResult.getResponse() != null) {
            String responseBody = asyncResult.getResponse().getContentAsString();
            System.out.println("testByWebClientWithOid /api/gemini \n Response: " + responseBody);
        }
        assertTrue(asyncResult.getResponse() != null && asyncResult.getResponse().getStatus() == 200);
    }

    @Test
    public void testByRestTemplateWithOAuth() throws Exception {
        MvcResult asyncResult = mockMvc.perform(post("/api/gemini/query")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .with(oidcLogin()) // <— correct for MVC
        ).andDo(print())
                .andExpect(request().asyncStarted())
                .andReturn();

        // Set the timeout for the async context (optional, default is usually 30s)
        // This must be done *after* the initial request returns but *before* the async result is ready
        asyncResult.getRequest().getAsyncContext().setTimeout(customTimeout); // 10 seconds

        // Wait for the async result and perform subsequent assertions via asyncDispatch
        mockMvc.perform(asyncDispatch(asyncResult))
                .andExpect(status().isOk()) // Now assert the final status code
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)) // Assert content type
                .andExpect(content().string(containsString("quantum"))); // Assert content body

        if (asyncResult.getResponse() != null) {
            String responseBody = asyncResult.getResponse().getContentAsString();
            System.out.println("testByRestTemplateWithOAuth /api/gemini/query \n Response: " + responseBody);
        }
        assertTrue(asyncResult.getResponse() != null && asyncResult.getResponse().getStatus() == 200);
    }

}
