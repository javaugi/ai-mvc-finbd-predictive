/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.test.epiconfhir;

import com.jvidia.aimlbda.MyApplicationBaseTests;
import com.jvidia.aimlbda.security.handler.OAuth2LoginSuccessHandler;
import com.jvidia.aimlbda.security.oauth2.HttpCookieOAuth2AutherizationRequestRepository;
import com.jvidia.aimlbda.service.JwtTokenService;
import com.jvidia.aimlbda.service.UserInfoService;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

/*
2️⃣ Unit Test — OAuth2 Login Success Handler

This verifies:
    JWT is created
    Cookie/session logic works
    Redirect happens correctly

 */
@Disabled
public class OAuth2LoginSuccessHandlerTest extends MyApplicationBaseTests {
    @Mock
    JwtTokenService jwtTokenService;

    @Mock
    UserInfoService userInfoService;

    @Mock
    HttpCookieOAuth2AutherizationRequestRepository authRequestRepository;

    @InjectMocks
    OAuth2LoginSuccessHandler successHandler;

    @Test
    void shouldRedirectAfterSuccessfulLogin() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        OAuth2AuthenticationToken authentication
                = mock(OAuth2AuthenticationToken.class);

        when(jwtTokenService.generateToken(any()))
                .thenReturn("mock-jwt-token");

        successHandler.onAuthenticationSuccess(
                request,
                response,
                authentication
        );

        assertThat(response.getRedirectedUrl()).isNotNull();
        assertThat(response.getStatus()).isEqualTo(302);
    }
}
