/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.config;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.RestClientAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Configuration
public class SmartFhirConfig {

    @Value("${smart.fhir.server-url}")
    private String fhirServerUrl;

    @Value("${smart.fhir.client-id}")
    private String clientId;

    @Autowired
    private Environment env;

    public static List<String> clients = Arrays.asList("google", "github", "epic", "auth0", "keycloak", "facebook");
    public static String CLIENT_PROPERTY_KEY = "spring.security.oauth2.client.registration.";

    //@Bean - If we’re not working with a Spring Boot application, we’ll need to define a ClientRegistrationRepository bean 
    public ClientRegistrationRepository clientRegistrationRepository() {
        List<ClientRegistration> registrations = clients.stream()
                .map(c -> getRegistration(c))
                .filter(registration -> registration != null)
                .collect(Collectors.toList());

        return new InMemoryClientRegistrationRepository(registrations);
    }

    private ClientRegistration getRegistration(String client) {
        String registrationClientId = env.getProperty(CLIENT_PROPERTY_KEY + client + ".client-id");
        log.debug("getRegistration clientId={}", registrationClientId);
        if (registrationClientId == null) {
            return null;
        }

        String clientSecret = env.getProperty(CLIENT_PROPERTY_KEY + client + ".client-secret");
        log.debug("getRegistration clientSecret={}", clientSecret);

        if (client.equals("google")) {
            return CommonOAuth2Provider.GOOGLE.getBuilder(client)
                    .clientId(registrationClientId).clientSecret(clientSecret).build();
        }
        if (client.equals("facebook")) {
            return CommonOAuth2Provider.FACEBOOK.getBuilder(client)
                    .clientId(registrationClientId).clientSecret(clientSecret).build();
        }
        return null;
    }

    //@Bean - this causes google and github logon failure
    public ClientRegistrationRepository inMemClientRegistrationRepository() {
        log.debug("SmartFhirConfig clientRegistrationRepository clientId={}, fhirServerUrl={}", clientId, fhirServerUrl);
        return new InMemoryClientRegistrationRepository(
                ClientRegistration.withRegistrationId("epicfhir")
                        .clientId("b5b466c3-558b-4ee4-b4d4-f91e6e860728")
                        .clientSecret("") // Make sure this is set
                        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                        .redirectUri("https://contractile-unruefully-madilynn.ngrok-free.dev/login/oauth2/code/epicfhir")
                        .scope("openid", "fhirUser")
                        .authorizationUri("https://fhir.epic.com/interconnect-fhir-oauth/oauth2/authorize")
                        .tokenUri("https://fhir.epic.com/interconnect-fhir-oauth/oauth2/token")
                        .userInfoUri("https://fhir.epic.com/interconnect-fhir-oauth/oauth2/userinfo")
                        .userNameAttributeName("sub")
                        .clientName("Epic FHIR")
                        .build()
        );
    }

    /*
    @Bean
    public ProviderSettings providerSettings() {
        return ProviderSettings.builder()
                .issuer("https://fhir.epic.com")
                .authorizationEndpoint("https://fhir.epic.com/interconnect-fhir-oauth/oauth2/authorize")
                .tokenEndpoint("https://fhir.epic.com/interconnect-fhir-oauth/oauth2/token")
                .jwkSetEndpoint("https://fhir.epic.com/interconnect-fhir-oauth/oauth2/certs")
                .userInfoEndpoint("https://fhir.epic.com/interconnect-fhir-oauth/oauth2/userinfo")
                .build();
    }
    // */

    @Bean
    public WebClient smartFhirWebClient() {
        return WebClient.builder()
                .baseUrl(fhirServerUrl)
                .defaultHeader("Accept", "application/fhir+json")
                .defaultHeader("Content-Type", "application/fhir+json")
                .build();
    }

    public Map<String, Object> getSmartLaunchParameters() {
        Map<String, Object> params = new HashMap<>();

        // SMART launch parameters
        params.put("client-id", clientId);
        params.put("aud", fhirServerUrl);
        //params.put("launch", ""); // Will be populated during EHR launch
        params.put("scope", "openid fhirUser");

        return params;
    }

    @Bean
    public RestClientAuthorizationCodeTokenResponseClient authorizationCodeAccessTokenResponseClient() {
        return new RestClientAuthorizationCodeTokenResponseClient();
    }

    @Bean
    public OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> customAccessTokenResponseClient(RestClient restClient) {
        return request -> {
            OAuth2AuthorizationCodeGrantRequest grantRequest = request;

            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("grant_type", "authorization_code");
            formData.add("code", grantRequest.getAuthorizationExchange().getAuthorizationResponse().getCode());
            formData.add("redirect_uri", grantRequest.getAuthorizationExchange().getAuthorizationRequest().getRedirectUri());
            formData.add("client_id", grantRequest.getClientRegistration().getClientId());
            log.debug("customAccessTokenResponseClient clientId={}", grantRequest.getClientRegistration().getClientId());

            return restClient
                    .post()
                    .uri(grantRequest.getClientRegistration().getProviderDetails().getTokenUri())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.USER_AGENT, "My-SMART-App")
                    .body(formData)
                    .retrieve()
                    .body(OAuth2AccessTokenResponse.class);
        };
    }

    @Bean
    public RestOperations restOperations() {
        RestTemplate restTemplate = new RestTemplate();

        // Add request/response logging interceptors
        restTemplate.setInterceptors(Arrays.asList(
                requestLoggingInterceptor(),
                responseLoggingInterceptor()
        ));

        return restTemplate;
    }

    private ClientHttpRequestInterceptor requestLoggingInterceptor() {
        return (request, body, execution) -> {
            log.debug("=== OAuth2 Token Request requestLoggingInterceptor ===");
            log.debug("\n OAuth2 Token Request URL: {}, Method: {}, \n Request Headers: {}, \n Request Body: {} ",
                    request.getURI(), request.getMethod(), request.getHeaders(), new String(body, StandardCharsets.UTF_8));

            if (body != null && body.length > 0) {
                String bodyStr = new String(body, StandardCharsets.UTF_8);
                log.debug("Body: {}", bodyStr);

                // Parse and log form parameters
                Map<String, String> params = parseFormUrlEncoded(bodyStr);
                log.debug("Form Parameters: {}", params);

                // Check for PKCE code_verifier
                if (params.containsKey("code_verifier")) {
                    log.debug("PKCE code_verifier is present");
                }
            }

            return execution.execute(request, body);
        };
    }

    private ClientHttpRequestInterceptor responseLoggingInterceptor() {
        return (request, body, execution) -> {
            ClientHttpResponse response = execution.execute(request, body);
            log.debug("=== OAuth2 Token Response responseLoggingInterceptor ===");
            log.debug("Status: {}", response.getStatusCode());
            log.debug("Headers: {}", response.getHeaders());

            // Read and log response body
            byte[] responseBody = StreamUtils.copyToByteArray(response.getBody());
            String responseBodyStr = new String(responseBody, StandardCharsets.UTF_8);
            log.debug("Response Body: {}", responseBodyStr);

            // Return a new response with the body restored
            return new ClientHttpResponse() {
                @Override
                public HttpStatusCode getStatusCode() throws IOException {
                    return response.getStatusCode();
                }

                @Override
                public String getStatusText() throws IOException {
                    return response.getStatusText();
                }

                @Override
                public void close() {
                    response.close();
                }

                @Override
                public InputStream getBody() {
                    return new ByteArrayInputStream(responseBody);
                }

                @Override
                public HttpHeaders getHeaders() {
                    return response.getHeaders();
                }
            };
        };
    }

    private Map<String, String> parseFormUrlEncoded(String formData) {
        Map<String, String> params = new HashMap<>();
        if (formData != null && !formData.isEmpty()) {
            String[] pairs = formData.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length == 2) {
                    try {
                        params.put(
                                URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8.name()),
                                URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8.name())
                        );
                    } catch (UnsupportedEncodingException e) {
                        // Ignore
                    }
                }
            }
        }
        return params;
    }
}
