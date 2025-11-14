/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.service.pact;

import com.jvidia.aimlbda.entity.TestUser;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class PactSubTestUserService {

	private final WebClient webClient;

    public PactSubTestUserService(@Value("${pact.user-service.url}") String baseUrl) {
		this.webClient = WebClient.builder()
			.baseUrl(baseUrl)
			.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.build();
	}

    public TestUser getUserById(Long userId) {
        return webClient.get().uri("/api/testonly/{id}", userId)
                .retrieve()
                .bodyToMono(TestUser.class)
                .block();
	}

    public TestUser createUser(TestUser user) {
        return webClient.post().uri("/api/testonly")
                .bodyValue(user)
                .retrieve()
                .bodyToMono(TestUser.class)
                .block();
    }

    public List<TestUser> getUsersByFirstName(String firstName) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/testonly").queryParam("firstName", firstName).build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<TestUser>>() {
                })
                .block();
    }

}
