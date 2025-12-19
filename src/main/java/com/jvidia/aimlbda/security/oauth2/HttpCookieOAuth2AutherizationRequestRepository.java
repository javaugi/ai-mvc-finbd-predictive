package com.jvidia.aimlbda.security.oauth2;

import com.jvidia.aimlbda.security.utils.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

@Configuration
public class HttpCookieOAuth2AutherizationRequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    private static final String OAUTH2_AUTHERIZATION_REQUEST_COOKIE_NAME = "oauth_auth_request";
    private static final String REDIRECT_PARAM_COOKIE_NAME = "redirect_uri";
    private static final int cookieMaxAgeInSeconds = 180;

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        return CookieUtil.getCookie(request, OAUTH2_AUTHERIZATION_REQUEST_COOKIE_NAME)
                .map(cookie -> CookieUtil.deserialize(cookie, OAuth2AuthorizationRequest.class))
                .orElse(null);
    }

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
        if(authorizationRequest == null) {
            CookieUtil.deleteCookie(request, response, OAUTH2_AUTHERIZATION_REQUEST_COOKIE_NAME);
            CookieUtil.deleteCookie(request, response, REDIRECT_PARAM_COOKIE_NAME);
            return;
        }

        CookieUtil.setCookie(response, OAUTH2_AUTHERIZATION_REQUEST_COOKIE_NAME, CookieUtil.searlizeCookie(authorizationRequest), cookieMaxAgeInSeconds);
        String redirectUriAfterLogin =request.getParameter(REDIRECT_PARAM_COOKIE_NAME);
        if(redirectUriAfterLogin != null) {
            CookieUtil.setCookie(response, REDIRECT_PARAM_COOKIE_NAME, redirectUriAfterLogin, cookieMaxAgeInSeconds);
        }
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) {
        return this.loadAuthorizationRequest(request);
    }

    public void removeAuthorizationRequestCookie(HttpServletRequest request, HttpServletResponse response) {
        CookieUtil.deleteCookie(request, response, OAUTH2_AUTHERIZATION_REQUEST_COOKIE_NAME);
        CookieUtil.deleteCookie(request, response, REDIRECT_PARAM_COOKIE_NAME);
    }
}
