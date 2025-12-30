/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.controller;

import com.jvidia.aimlbda.security.utils.OAuth2DebugUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/debug")
public class DebugController {

    @Value("${smart.fhir.server-url}")
    private String fhirServerUrl;

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    @Autowired
    private OAuth2DebugUtil debugUtil;

    @GetMapping("/oauth2/config")
    public ResponseEntity<Map<String, Object>> getOAuth2Config() {
        Map<String, Object> config = new HashMap<>();

        // Get all client registrations
        Iterable<ClientRegistration> registrations
                = (Iterable<ClientRegistration>) clientRegistrationRepository;

        List<Map<String, String>> clients = new ArrayList<>();
        registrations.forEach(registration -> {
            Map<String, String> clientInfo = new HashMap<>();
            clientInfo.put("id", registration.getRegistrationId());
            clientInfo.put("clientId", registration.getClientId());
            clientInfo.put("authorizationUri",
                    registration.getProviderDetails().getAuthorizationUri());
            clientInfo.put("redirectUri", registration.getRedirectUri());
            clientInfo.put("scopes", String.join(",", registration.getScopes()));
            clients.add(clientInfo);

            // Debug log
            debugUtil.debugClientRegistration(registration);
        });

        config.put("clients", clients);
        config.put("serverPort", 8088);
        config.put("baseUrl", "http://localhost:8088");

        return ResponseEntity.ok(config);
    }

    @GetMapping("/test/epic-auth")
    public String testEpicAuth() {
        ClientRegistration epicRegistration
                = clientRegistrationRepository.findByRegistrationId("epicfhir");

        if (epicRegistration == null) {
            return "Epic FHIR client registration not found!";
        }

        // Build authorization URL manually for testing
        String authUrl = UriComponentsBuilder
                .fromUriString(epicRegistration.getProviderDetails().getAuthorizationUri())
                .queryParam("response_type", "code")
                .queryParam("client_id", epicRegistration.getClientId())
                .queryParam("redirect_uri", epicRegistration.getRedirectUri())
                .queryParam("scope", String.join(" ", epicRegistration.getScopes()))
                .queryParam("state", UUID.randomUUID().toString())
                .queryParam("aud", fhirServerUrl)
                .build()
                .toUriString();

        return "Test Authorization URL: " + authUrl;
    }
}
