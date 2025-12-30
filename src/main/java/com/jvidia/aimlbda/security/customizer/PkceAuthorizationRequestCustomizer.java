/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.security.customizer;

import com.jvidia.aimlbda.security.utils.PkceUtil;
import java.security.NoSuchAlgorithmException;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.PkceParameterNames;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PkceAuthorizationRequestCustomizer implements Consumer<OAuth2AuthorizationRequest.Builder> {

    @Value("${smart.fhir.server-url}")
    private String fhirServerUrl;

    @Autowired
    private PkceUtil pkceUtil;

    @Override
    public void accept(OAuth2AuthorizationRequest.Builder builder) {
        try {
            // Generate PKCE code verifier and challenge
            String codeVerifier = pkceUtil.generateCodeVerifier();
            String codeChallenge = pkceUtil.generateCodeChallenge(codeVerifier);
            log.debug("PkceAuthorizationRequestCustomizer codeVerifier={}, codeChallenge={}", codeVerifier, codeChallenge);

            // Add PKCE parameters to authorization request
            builder.attributes(attrs -> {
                attrs.put(PkceParameterNames.CODE_VERIFIER, codeVerifier);
                attrs.put("registration_id", "epicfhir");
            }).additionalParameters(params -> {
                params.put(PkceParameterNames.CODE_CHALLENGE, codeChallenge);
                params.put(PkceParameterNames.CODE_CHALLENGE_METHOD, "S256");
                params.put("aud", fhirServerUrl);
                // SMART on FHIR specific parameters
                params.put("response_type", "code");
            });

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to generate PKCE parameters", e);
        }
    }
}
