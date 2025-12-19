/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.test.webtestclient;

import com.jvidia.aimlbda.MyApplicationBaseTests;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

public class WebTestClientHealthTest extends MyApplicationBaseTests {
    @Autowired
    private WebTestClient webTestClient;


    @Test
    public void shouldReturnHealthStatusUp() {
        // Test the Spring Boot Actuator health endpoint
        webTestClient.get()
                .uri(HEALTH_URL)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.status")
                .isEqualTo("UP");
    }

}
