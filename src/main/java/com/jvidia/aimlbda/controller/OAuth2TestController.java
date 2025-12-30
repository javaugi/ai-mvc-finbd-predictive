/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.controller;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.endpoint.PkceParameterNames;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.InvalidUrlException;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/test")
public class OAuth2TestController {

    @Value("${smart.fhir.server-url}")
    private String fhirServerUrl;

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    @GetMapping("/oauth2-config")
    public Map<String, Object> getOAuth2Config() {
        Map<String, Object> config = new HashMap<>();

        // Get Epic FHIR configuration
        ClientRegistration epic = clientRegistrationRepository.findByRegistrationId("epicfhir");

        if (epic != null) {
            Map<String, String> epicConfig = new HashMap<>();
            epicConfig.put("clientId", epic.getClientId());
            epicConfig.put("authorizationUri", epic.getProviderDetails().getAuthorizationUri());
            epicConfig.put("tokenUri", epic.getProviderDetails().getTokenUri());
            epicConfig.put("redirectUri", epic.getRedirectUri());
            epicConfig.put("scopes", String.join(",", epic.getScopes()));
            epicConfig.put("clientAuthenticationMethod",
                    epic.getClientAuthenticationMethod().getValue());

            config.put("epicFhir", epicConfig);

            // Build a test authorization URL
            String testAuthUrl = buildTestAuthorizationUrl(epic);
            config.put("testAuthorizationUrl", testAuthUrl);
        }

        return config;
    }

    private String buildTestAuthorizationUrl(ClientRegistration registration) {
        try {
            // Generate PKCE parameters
            String codeVerifier = generateCodeVerifier();
            String codeChallenge = generateCodeChallenge(codeVerifier);

            return UriComponentsBuilder
                    .fromUriString(registration.getProviderDetails().getAuthorizationUri())
                    .queryParam("response_type", "code")
                    .queryParam("client_id", registration.getClientId())
                    .queryParam("redirect_uri", registration.getRedirectUri())
                    .queryParam("scope", String.join(" ", registration.getScopes()))
                    .queryParam("state", UUID.randomUUID().toString())
                    .queryParam(PkceParameterNames.CODE_CHALLENGE, codeChallenge)
                    .queryParam(PkceParameterNames.CODE_CHALLENGE_METHOD, "S256")
                    .queryParam("aud", fhirServerUrl)
                    .build()
                    .toUriString();

        } catch (NoSuchAlgorithmException | InvalidUrlException e) {
            return "Error building URL: " + e.getMessage();
        }
    }

    private String generateCodeVerifier() {
        SecureRandom sr = new SecureRandom();
        byte[] code = new byte[32];
        sr.nextBytes(code);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(code);
    }

    private String generateCodeChallenge(String codeVerifier) throws NoSuchAlgorithmException {
        byte[] bytes = codeVerifier.getBytes(StandardCharsets.US_ASCII);
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(bytes, 0, bytes.length);
        byte[] digest = md.digest();
        return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
    }
}
