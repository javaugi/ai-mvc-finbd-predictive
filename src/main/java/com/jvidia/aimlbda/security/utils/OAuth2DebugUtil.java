/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.security.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OAuth2DebugUtil {

    public void debugAuthorizationRequest(OAuth2AuthorizationRequest request) {
        log.debug("=== OAuth2 Authorization Request Debug ===");
        log.debug("Authorization URI: {}", request.getAuthorizationUri());
        log.debug("Client ID: {}", request.getClientId());
        log.debug("Redirect URI: {}", request.getRedirectUri());
        log.debug("Scopes: {}", request.getScopes());
        log.debug("State: {}", request.getState());
        log.debug("Additional Parameters: {}", request.getAdditionalParameters());
        log.debug("Attributes: {}", request.getAttributes());
        log.debug("=== End Debug ===");
    }

    public void debugClientRegistration(ClientRegistration registration) {
        log.debug("=== Client Registration Debug ===");
        log.debug("Registration ID: {}", registration.getRegistrationId());
        log.debug("Client ID: {}", registration.getClientId());
        log.debug("Client Secret: {}", registration.getClientSecret());
        log.debug("Client Authentication Method: {}",
                registration.getClientAuthenticationMethod());
        log.debug("Authorization Grant Type: {}",
                registration.getAuthorizationGrantType());
        log.debug("Redirect URI: {}", registration.getRedirectUri());
        log.debug("Scopes: {}", registration.getScopes());
        log.debug("Provider Details:");
        log.debug("  Authorization URI: {}",
                registration.getProviderDetails().getAuthorizationUri());
        log.debug("  Token URI: {}",
                registration.getProviderDetails().getTokenUri());
        log.debug("  User Info URI: {}",
                registration.getProviderDetails().getUserInfoEndpoint().getUri());
        log.debug("  JWK Set URI: {}",
                registration.getProviderDetails().getJwkSetUri());
        log.debug("=== End Debug ===");
    }
}
