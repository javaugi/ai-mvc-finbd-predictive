package com.jvidia.aimlbda.security.filter;

import com.jvidia.aimlbda.service.JwtTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;
    private final UserDetailsService userDetailsService;
    private final List<String> excludedPaths = List.of(
            "/public/",
            "/auth/login",
            "/auth/signup",
            "/auth/logout",
            "/actuator",
            "/error",
            "/h2-console",
            "/favicon.ico",
            "/webjars",
            "/swagger-ui",
            "/v3/api-docs",
            "/api/testonly"
    );

    @Autowired
    private HandlerExceptionResolver handlerExceptionResolver;

    public JwtAuthenticationFilter(JwtTokenService jwtTokenProvider, UserDetailsService userDetailsService) {
        this.jwtTokenService = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return shouldNotFilterForSessionAuth(request);
    }

    private boolean shouldNotFilterForSessionAuth(HttpServletRequest request) {
        String path = request.getRequestURI();
        return excludedPaths.stream().anyMatch(path::startsWith)
                || hasOAuth2SessionAuthentication(request);
    }

    private boolean hasOAuth2SessionAuthentication(HttpServletRequest request) {
        // Check if there's an existing OAuth2 session
        var existingAuth = SecurityContextHolder.getContext().getAuthentication();
        return existingAuth != null
                && existingAuth.isAuthenticated()
                && existingAuth.getPrincipal() instanceof OAuth2User;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        // Skip JWT processing for session-based OAuth2 endpoints
        if (shouldNotFilterForSessionAuth(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = jwtTokenService.resolveToken(request);
            log.debug("doFilterInternal token {}", token);
            if (token != null && jwtTokenService.validateToken(token)) {
                String username = jwtTokenService.getUsernameFromToken(token);
                log.debug("doFilterInternal getUsernameFromToken(token) username {}", username);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                log.debug("doFilterInternal loadUserByUsername username {} \n userDetails {}", username, userDetails);
                UsernamePasswordAuthenticationToken authentication
                        = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("JWT authentication successful for user {}, authorities {} ", username, userDetails.getAuthorities());
            }
        } catch (UsernameNotFoundException ex) {
            log.error("Error doFilterInternal ", ex);
            handlerExceptionResolver.resolveException(request, response, null, ex);
            return;
        }

        filterChain.doFilter(request, response);
    }

}
