/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.clients;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class RestControllerClient {

    final WebClient webClient;

    public RestControllerClient() {
        webClient = WebClient.builder()
                .baseUrl("http://localhost:8088")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    // Method to make a GET request
    public void getData(String uri, String paramValue) {

        webClient.get()
                .uri(uri, paramValue) // Use path variable properly
                .retrieve()
                .bodyToFlux(Object.class)
                .doOnError(e -> {
                    System.out.println("Error getData: " + e);
                })
                .subscribe(
                        result -> System.out.println("Result: " + result),
                        error -> System.err.println("Error: " + error),
                        () -> System.out.println("Completed")
                );
    }

}
