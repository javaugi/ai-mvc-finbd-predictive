package com.jvidia.aimlbda.security.handler;

import com.jvidia.aimlbda.entity.UserInfo;
import com.jvidia.aimlbda.entity.UserRole;
import com.jvidia.aimlbda.security.oauth2.HttpCookieOAuth2AutherizationRequestRepository;
import com.jvidia.aimlbda.service.JwtTokenService;
import com.jvidia.aimlbda.service.UserInfoService;
import com.jvidia.aimlbda.utils.CookieUtils;
import com.jvidia.aimlbda.utils.LogUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.coyote.BadRequestException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

@lombok.extern.slf4j.Slf4j
@Component
@lombok.RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final String REDIRECT_PARAM_COOKIE_NAME = "redirect_uri";

    private final JwtTokenService jwtTokenService;
    private final UserInfoService userInfoService;
    private final HttpCookieOAuth2AutherizationRequestRepository httpCookieOAuth2AutherizationRequestRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        log.debug("onAuthenticationSuccess isAuthenticated {} username {} ", authentication.isAuthenticated(), authentication.getName());
        // Ensure authentication is stored in security context
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // Store authentication in session (CRITICAL for session-based auth)
        request.getSession().setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

        OAuth2User authUser = (OAuth2User) authentication.getPrincipal();
        LogUtils.logMap("OAuth2LoginSuccessHandler.onAuthenticationSuccess", authUser.getAttributes());

        String email = authUser.getAttribute("email");
        UserInfo savedUser = this.userInfoService.findByEmail(email).orElse(null);
        List<UserRole> userRoles = savedUser != null ? savedUser.getUserRoles() : null;
        Collection<? extends GrantedAuthority> authorities = userRoles != null ? userRoles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getRole().getRole()))
                .toList() : null;
        String token = jwtTokenService.generateToken(email, authorities);

        String redirectUrl = UriComponentsBuilder.fromUriString(determineTargetUrl(request, response, authentication))
                .queryParam("token", token)
                .build().toUriString();
        log.debug("onAuthenticationSuccess email {}, response.isCommitted() {} \n redirectUrl {} ", email, response.isCommitted(), redirectUrl);
        if (response.isCommitted()) {
            return;
        }

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }

    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        httpCookieOAuth2AutherizationRequestRepository.removeAuthorizationRequestCookie(request, response);
    }

    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Optional<String> redirectUri = CookieUtils.getCookie(request, REDIRECT_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);
        log.debug("determineTargetUrl redirectUri {} ", (redirectUri.isPresent() ? redirectUri.get() : ""));

        if (redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
            try {
                throw new BadRequestException("Unauthorized Redirect URI");
            } catch (BadRequestException e) {
                throw new RuntimeException(e);
            }
        }

        return redirectUri.orElse(getDefaultTargetUrl());
    }

    protected boolean isAuthorizedRedirectUri(String url) {
        URI clientRedirectUri = URI.create(url);
        log.debug("isAuthorizedRedirectUri clientRedirectUri {}", clientRedirectUri);
        return true;
    }

    protected String appendTokenToUrl(String url, String token) {
        StringBuilder urlWithToken = new StringBuilder(url);
        if (url.contains("?")) {
            urlWithToken.append("&");
        } else {
            urlWithToken.append("?");
        }
        urlWithToken.append("token=").append(token);
        return urlWithToken.toString();
    }
}
