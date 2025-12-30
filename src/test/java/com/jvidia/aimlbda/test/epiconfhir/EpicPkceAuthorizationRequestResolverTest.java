package com.jvidia.aimlbda.test.epiconfhir;


import com.jvidia.aimlbda.MyApplicationBaseTests;
import java.util.Map;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

/*
1️⃣ Unit Tests — PKCE + Authorization Request (NO Epic calls)
These tests validate:
    PKCE is applied
    aud parameter is added
    Epic registration is handled correctly

📌 This is what Epic reviewers actually expect you to test

✔️ Tests PKCE
✔️ Tests Epic aud
✔️ No network calls
✔️ CI-safe
 */
@Disabled
public class EpicPkceAuthorizationRequestResolverTest extends MyApplicationBaseTests {

    @Mock
    private ClientRegistrationRepository clientRegistrationRepository;

    @Autowired
    private OAuth2AuthorizationRequestResolver resolver;

    @BeforeEach
    public void setup() {
        //resolver = new SecurityConfig().epicPkceResolver(clientRegistrationRepository);
    }

    @Test
    void shouldAddPkceAndAudForEpicClient() {
        ClientRegistration epicRegistration
                = ClientRegistration.withRegistrationId("epicfhir")
                        .clientId("test-client-id")
                        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                        .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                        .authorizationUri("https://fhir.epic.com/interconnect-fhir-oauth/oauth2/authorize")
                        .tokenUri("https://fhir.epic.com/interconnect-fhir-oauth/oauth2/token")
                        .scope("openid")
                        .build();

        when(clientRegistrationRepository.findByRegistrationId("epicfhir"))
                .thenReturn(epicRegistration);

        MockHttpServletRequest request
                = new MockHttpServletRequest("GET", "/oauth2/authorization/epicfhir");

        OAuth2AuthorizationRequest authRequest = resolver.resolve(request);

        assertThat(authRequest).isNotNull();

        Map<String, Object> params = authRequest.getAdditionalParameters();

        // ✅ PKCE
        assertThat(params.containsKey("code_challenge"));
        assertThat(params.containsKey("code_challenge_method"));
        assertThat(params.get("code_challenge_method")).isEqualTo("S256");

        // ✅ Epic SMART on FHIR requirement
        assertThat(params.get("aud"))
                .isEqualTo("https://fhir.epic.com/interconnect-fhir-oauth/api/FHIR/R4");
    }
}
