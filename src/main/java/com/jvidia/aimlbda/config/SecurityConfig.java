/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.config;

import com.jvidia.aimlbda.repository.RoleRepository;
import com.jvidia.aimlbda.security.RestAuthenticationEntryPoint;
import com.jvidia.aimlbda.security.filter.JwtAuthenticationFilter;
import com.jvidia.aimlbda.security.handler.CustomAccessDeniedHandler;
import com.jvidia.aimlbda.security.handler.OAuth2LoginSuccessHandler;
import com.jvidia.aimlbda.security.oauth2.HttpCookieOAuth2AutherizationRequestRepository;
import com.jvidia.aimlbda.service.JwtTokenService;
import com.jvidia.aimlbda.service.OAuthUserService;
import com.jvidia.aimlbda.service.RoleService;
import com.jvidia.aimlbda.service.UserInfoService;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/*
2. Key technical difference
    OAuth2                  OpenID Connect

    Authorization only      Authentication + Identity
    Uses Access Token       Uses ID Token (JWT)
    No standard user info	Standard /userinfo endpoint
    No ID standard          Standard claims (sub, email, name, picture)
 */
@lombok.extern.slf4j.Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Value("application.base-url")
    private String appHostPort;

    private final String[] PERMIT_ALL_PATHS = List.of("/public/**", "/auth/login", "/auth/signup", "/auth/logout",
            "/error", "/h2-console", "/h2-console/**", "/favicon.ico", "/webjars", "/webjars/**",
            "/actuator/**", "/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html", "/api/testonly/**")
            .toArray(new String[0]);

    private final JwtTokenService jwtTokenService;
    private final UserInfoService userInfoService;
    private final RoleRepository roleRepository;
    private final RoleService roleService;
    private final HttpCookieOAuth2AutherizationRequestRepository httpCookieOAuth2AutherizationRequestRepository;
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final UserDetailsService userDetailsService;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    public SecurityConfig(JwtTokenService jwtTokenService, UserInfoService userInfoService, RoleRepository roleRepository,
            HttpCookieOAuth2AutherizationRequestRepository httpCookieOAuth2AutherizationRequestRepository,
            UserDetailsService userDetailsService, RestAuthenticationEntryPoint restAuthenticationEntryPoint,
            CustomAccessDeniedHandler customAccessDeniedHandler, RoleService roleService) {
        this.jwtTokenService = jwtTokenService;
        this.userInfoService = userInfoService;
        this.roleRepository = roleRepository;
        this.httpCookieOAuth2AutherizationRequestRepository = httpCookieOAuth2AutherizationRequestRepository;
        this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
        this.userDetailsService = userDetailsService;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
        this.roleService = roleService;
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        log.debug("Bean jwtAuthenticationFilter ..... ");
        return new JwtAuthenticationFilter(jwtTokenService, userDetailsService);
    }

    /*
    Security Filter chain: Contains policies for all the security - Oauth2 + OpenID Connect (OIDC)
        1. Use OpenID Connect (OIDC) + OAuth2 + JWT tokens
        2. React App → Spring Boot API → OAuth2 providers
        3. Google always uses OpenID Connect for user identity.
        4. GitHub is the exception — GitHub is NOT a full OIDC provider by default (unless using GitHub Actions OIDC). It is pure OAuth2.
        5. Internal User: Username + Password -> JWT
            If you want to make it true OIDC, you would need: An internal Authorization Server like:
                (1) Keycloak, (2) Okta, (3)Auth0,   or (4) Azure AD / Entra ID
        6. The BEST SSO approach for you (Modern + Simple)
            Since you already use Spring Boot + React: Use OpenID Connect (OIDC) + OAuth2 + JWT tokens
            What you need now is:
                (1) Choose who is your Identity Provider (SSO Server)
                (2) Make your Spring Boot apps trust it
                (3) Keycloak (BEST for full control): Free, open-source
                    You can add:
                        (1) Google, (2) GitHub, and (3) Internal users
                    Handles:
                        (1) SSO, (2) MFA, (3) Roles, (4) User federation, and (5) Industry-standard choice
        7. Architecture for SSO
            React App → Identity Provider (Keycloak / Azure / Spring Auth Server)
                     ↓ issues JWT
            Spring Boot services ← validate JWT
            Other apps       ← validate same JWT
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.debug("securityFilterChain ..... ");
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(authorizeRequests
                        -> authorizeRequests
                        .requestMatchers(PERMIT_ALL_PATHS).permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                .authorizationEndpoint(authEndpoint -> authEndpoint
                .authorizationRequestRepository(httpCookieOAuth2AutherizationRequestRepository))
                .redirectionEndpoint(redirect -> redirect.baseUri("/login/oauth2/code/*"))
                .userInfoEndpoint(userInfo -> userInfo.userService(oAuth2UserService()))
                .successHandler(new OAuth2LoginSuccessHandler(jwtTokenService, userInfoService, httpCookieOAuth2AutherizationRequestRepository)))
                .logout(logout -> logout
                .logoutUrl("/auth/logout") // Single logout endpoint
                .logoutSuccessHandler((request, response, authentication) -> {
                    // Determine the appropriate redirect URL based on the request origin
                    String referer = request.getHeader("Referer");
                    String origin = request.getHeader("Origin");

                    String redirectUrl = determineLogoutRedirectUrl(referer, origin);
                    response.sendRedirect(redirectUrl);
                })
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
                )
                .exceptionHandling(ex -> ex.authenticationEntryPoint(restAuthenticationEntryPoint)
                .accessDeniedHandler(customAccessDeniedHandler))
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                //.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        // This is necessary to show the H2 console in a frame
        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));
        return http.build();
    }

    //CSRF (Cross-Site Request Forgery):
    //CORS (Cross-Origin Resource Sharing):
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:8088",
                "http://localhost:5173", "http://localhost:3000", "http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type",
                "X-Auth-Token", "X-API-KEY", "X-User-Id", "X-User-Name"));
        configuration.setExposedHeaders(Arrays.asList("Authorization", "X-Auth-Token",
                "X-RateLimit-Remaining", "X-RateLimit-Reset"));
        configuration.setAllowCredentials(true); // Important for session cookies

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    protected String determineLogoutRedirectUrl(String referer, String origin) {
        log.debug("determineLogoutRedirectUrl referer {}, origin {}", referer, origin);
        // Default to React app
        String defaultRedirect = "http://localhost:3000/login";

        if (referer != null) {
            if (referer.contains("localhost:3000")) {
                return "http://localhost:3000/login?logout=true";
            } else if (referer.contains("localhost:5137")) {
                return "http://localhost:5137/login?logout=true";
            }
        }

        if (origin != null) {
            if (origin.contains("localhost:3000")) {
                return "http://localhost:3000/login?logout=true";
            } else if (origin.contains("localhost:5137")) {
                return "http://localhost:5137/login?logout=true";
            }
        }

        log.debug("determineLogoutRedirectUrl defaultRedirect {}", defaultRedirect);
        return defaultRedirect;
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        log.debug("authenticationManager ...");
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationProvider(authenticationProvider())
                .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        log.debug("authenticationProvider ...");
        return authenticationProvider;
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withSecretKey(jwtTokenService.getSecretKey()).build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*
    @Bean
    public PasswordEncoder passwordPlain() {
        return NoOpPasswordEncoder.getInstance();
    }
    // */
    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService() {
        log.debug("oAuth2UserService ...");
        return new OAuthUserService(userInfoService, roleService);
    }
}
