package com.jvidia.aimlbda.controller;

import com.jvidia.aimlbda.dto.LoginRequest;
import com.jvidia.aimlbda.dto.SignupRequest;
import com.jvidia.aimlbda.service.UserAuthSignupService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@lombok.extern.slf4j.Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    final UserAuthSignupService authSignupService;

    public AuthController(UserAuthSignupService userService) {
        this.authSignupService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        log.debug("login loginRequest {}", loginRequest);
        return authSignupService.authenticate(loginRequest);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> register(@Valid @RequestBody SignupRequest signupRequest) {
        log.debug("login signupRequest {}", signupRequest);
        return authSignupService.register(signupRequest);
    }

    private void debug(String method, String token, OAuth2User principal) {
        log.debug("method {} token {} \n principal {}", method, token, principal);
        if (principal != null) {
            log.debug("method {} name {}", method, principal.getAttribute("name"));
            log.debug("method {} email {}", method, principal.getAttribute("email"));
            log.debug("method {} authorities {}", method, principal.getAuthorities().stream()
                    .map(auth -> auth.getAuthority())
                    .toList());
        }
    }

    @GetMapping("/success")
    public Map<String, Object> authSuccess(@AuthenticationPrincipal OAuth2User principal,
            @RequestParam(value = "token", required = false) String token) {
        debug("authSuccess", token, principal);
        if (principal == null) {
            return Collections.singletonMap("authenticated", false);
        }

        return Map.of(
                "authenticated", true,
                "user", Map.of(
                        "name", principal.getAttribute("name"),
                        "email", principal.getAttribute("email"),
                        "picture", principal.getAttribute("picture")
                )
        );
    }

    @GetMapping("/user")
    public Map<String, Object> getUserInfo(@AuthenticationPrincipal OAuth2User principal,
            @RequestParam(value = "token", required = false) String token) {
        debug("getUserInfo", token, principal);
        if (principal == null) {
            return Map.of("authenticated", false);
        }

        return Map.of(
                "authenticated", true,
                "name", principal.getAttribute("name"),
                "email", principal.getAttribute("email"),
                "authorities", principal.getAuthorities().stream()
                        .map(auth -> auth.getAuthority())
                        .toList()
        );
    }

    @GetMapping("/checkOld")
    public Map<String, Object> authCheckOld(@AuthenticationPrincipal OAuth2User principal,
            @RequestParam(value = "token", required = false) String token) {
        debug("authCheck", token, principal);
        if (principal == null) {
            return Map.of("authenticated", false);
        }

        return Map.of(
                "authenticated", true,
                "user", Map.of(
                        "name", principal.getAttribute("name"),
                        "email", principal.getAttribute("email"),
                        "picture", principal.getAttribute("picture")
                )
        );
    }

    @GetMapping("/check")
    public Map<String, Object> authCheck(Authentication authentication) {
        log.debug("authCheck authentication {}", authentication);
        if (authentication == null || !authentication.isAuthenticated()) {
            return Map.of("authenticated", false);
        }

        String name = authentication.getName();
        authentication.getPrincipal();
        log.debug("authCheck name {} principal {} \n details {}", name, authentication.getPrincipal(), authentication.getDetails());

        return Map.of(
                "authenticated", true,
                "user", name
        );
    }

    @GetMapping("/session-info")
    public Map<String, Object> sessionInfo(@AuthenticationPrincipal OAuth2User principal,
            @RequestParam(value = "token", required = false) String token) {
        debug("session-info", token, principal);
        return Map.of(
                "sessionActive", principal != null,
                "authenticated", principal != null,
                "sessionBased", true,
                "user", principal != null ? Map.of(
                                "name", principal.getAttribute("name"),
                                "email", principal.getAttribute("email")
                        ) : null
        );
    }

    @PostMapping("/logout")
    public Map<String, String> logout(HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null) {
            // Manual logout
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }

        // Invalidate session
        request.getSession().invalidate();

        // Determine redirect URL based on origin
        String origin = request.getHeader("Origin");
        String referer = request.getHeader("Referer");

        String redirectUrl = determineRedirectUrl(origin, referer);

        Map<String, String> result = new HashMap<>();
        result.put("status", "success");
        result.put("redirectUrl", redirectUrl);
        result.put("message", "Logged out successfully");
        log.debug("logout origin {} referer {} \n result {}", origin, referer, result);

        return result;
    }

    @PostMapping("/logout-oauth")
    public void logoutOAuth(HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Handle OAuth2 logout
        if (auth instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) auth;
            String registrationId = oauthToken.getAuthorizedClientRegistrationId();

            // You can add provider-specific logout URLs here if needed
            // For example, for Google: 
            // "https://accounts.google.com/Logout"
            log.debug("logoutOAuth registrationId {}", registrationId);
        }

        // Perform standard logout
        new SecurityContextLogoutHandler().logout(request, response, auth);
        request.getSession().invalidate();

        // Redirect based on origin
        String origin = request.getHeader("Origin");
        String redirectUrl = origin != null ? origin + "/login" : "http://localhost:3000/login";

        log.debug("logoutOAuth origin {} redirectUrl {}", origin, redirectUrl);
        response.sendRedirect(redirectUrl + "?logout=success");
    }

    private String determineRedirectUrl(String origin, String referer) {
        if (origin != null) {
            if (origin.contains("localhost:3000")) {
                return "http://localhost:3000/login";
            } else if (origin.contains("localhost:5137")) {
                return "http://localhost:5137/login";
            }
        }

        if (referer != null) {
            if (referer.contains("localhost:3000")) {
                return "http://localhost:3000/login";
            } else if (referer.contains("localhost:5137")) {
                return "http://localhost:5137/login";
            }
        }

        return "http://localhost:3000/login";
    }
}

