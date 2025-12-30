/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.security.resolver;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

@Slf4j
public class DelegatingAuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

    private final OAuth2AuthorizationRequestResolver epicResolver;
    private final OAuth2AuthorizationRequestResolver defaultResolver;

    public DelegatingAuthorizationRequestResolver(OAuth2AuthorizationRequestResolver epicResolver,
            OAuth2AuthorizationRequestResolver defaultResolver) {
        this.epicResolver = epicResolver;
        this.defaultResolver = defaultResolver;
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        return delegate(request, null);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        return delegate(request, clientRegistrationId);
    }

    private OAuth2AuthorizationRequest delegate(HttpServletRequest request, String clientRegistrationId) {
        String uri = request.getRequestURI();
        log.debug("DelegatingAuthorizationRequestResolver delegate uri={}, clientRegistrationId={}", uri, clientRegistrationId);
        if (uri.contains("/oauth2/authorization/epicfhir")) {
            return (clientRegistrationId == null)
                    ? epicResolver.resolve(request)
                    : epicResolver.resolve(request, clientRegistrationId);
        }

        return (clientRegistrationId == null)
                ? defaultResolver.resolve(request)
                : defaultResolver.resolve(request, clientRegistrationId);
    }
}
