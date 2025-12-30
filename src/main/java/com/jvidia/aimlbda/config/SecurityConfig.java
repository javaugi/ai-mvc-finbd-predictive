/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.config;

import com.jvidia.aimlbda.repository.RoleRepository;
import com.jvidia.aimlbda.security.RestAuthenticationEntryPoint;
import com.jvidia.aimlbda.security.filter.JwtAuthenticationFilter;
import com.jvidia.aimlbda.security.handler.CustomAccessDeniedHandler;
import com.jvidia.aimlbda.security.utils.RedirectUtils;
import com.jvidia.aimlbda.security.handler.OAuth2LoginSuccessHandler;
import com.jvidia.aimlbda.security.oauth2.HttpCookieOAuth2AutherizationRequestRepository;
import com.jvidia.aimlbda.security.resolver.DelegatingAuthorizationRequestResolver;
import com.jvidia.aimlbda.security.utils.PkceUtil;
import com.jvidia.aimlbda.service.JwtTokenService;
import com.jvidia.aimlbda.service.OAuthUserService;
import com.jvidia.aimlbda.service.RoleService;
import com.jvidia.aimlbda.service.UserInfoService;
import com.jvidia.aimlbda.utils.LogUtil;
import java.security.NoSuchAlgorithmException;
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
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestCustomizers;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.endpoint.PkceParameterNames;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
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
@lombok.RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${smart.fhir.server-url}")
    private String fhirServerUrl;

    private final String[] PERMIT_ALL_PATHS = List.of("/", "/login/**",
            "/public/**", "/favicon.ico", "/webjars", "/webjars/**",
            "/auth/login", "/auth/signup", "/auth/logout", "/oauth2/**",
            "/error", "/h2-console", "/h2-console/**",
            "/actuator/**", "/swagger-ui/**", "/v3/api-docs/**",
            "/swagger-ui.html", "/api/testonly/**")
            .toArray(new String[0]);

    private final JwtTokenService jwtTokenService;
    private final UserInfoService userInfoService;
    private final RoleRepository roleRepository;
    private final RoleService roleService;
    private final HttpCookieOAuth2AutherizationRequestRepository httpCookieOAuth2AutherizationRequestRepository;
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final UserDetailsService userDetailsService;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final RedirectUtils redirectUtils;
    private final PkceUtil pkceUtil;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        log.debug("Bean jwtAuthenticationFilter ..... ");
        return new JwtAuthenticationFilter(jwtTokenService, userDetailsService);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, ClientRegistrationRepository repo) throws Exception {
        log.debug("securityFilterChain ..... ");
        OAuth2AuthorizationRequestResolver delegatingResolver = getDelegatingResolver(repo);

        http.csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(authorizeRequests
                        -> authorizeRequests
                        .requestMatchers(PERMIT_ALL_PATHS).permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                .authorizationEndpoint(authEndpoint -> authEndpoint
                .authorizationRequestRepository(httpCookieOAuth2AutherizationRequestRepository)
                .authorizationRequestResolver(delegatingResolver)
                )
                .redirectionEndpoint(redirect -> redirect.baseUri("/login/oauth2/code/*"))
                .userInfoEndpoint(userInfo -> userInfo.userService(oAuth2UserService()))
                .successHandler(new OAuth2LoginSuccessHandler(jwtTokenService, userInfoService, httpCookieOAuth2AutherizationRequestRepository)))
                .logout(logout -> logout
                .logoutSuccessUrl("/")
                .logoutUrl("/auth/logout") // Single logout endpoint
                .logoutSuccessHandler((request, response, authentication) -> {
                    // Determine the appropriate redirect URL based on the request origin
                    String referer = request.getHeader("Referer");
            String origin = request.getHeader("Origin");
            LogUtil.logRequest("SecurityConfig", request);
            String redirectUrl = redirectUtils.determineRedirectUrl(referer, origin);
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
                //.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
                .addFilterAfter(jwtAuthenticationFilter(), OAuth2AuthorizationRequestRedirectFilter.class);

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

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService() {
        log.debug("oAuth2UserService ...");
        return new OAuthUserService(userInfoService, roleService);
    }

    /*
    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService() {
        log.debug("Creating OAuth2UserService...");

        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        return userRequest -> {
            log.debug("Loading user for registration: {}", userRequest.getClientRegistration().getRegistrationId());
            try {
                OAuth2User user = delegate.loadUser(userRequest);
                log.debug("User loaded successfully: {}", user.getName());
                log.debug("User attributes: {}", user.getAttributes());

                return user;
            } catch (OAuth2AuthenticationException e) {
                log.error("Error loading OAuth2 user: {}", e.getMessage(), e);
                throw e;
            } catch (Exception e) {
                log.error("Unexpected error loading OAuth2 user: {}", e.getMessage(), e);
                throw new OAuth2AuthenticationException(
                        new OAuth2Error("user_load_error", e.getMessage(), null),
                        e
                );
            }
        };
    }
    // */
    public OAuth2AuthorizationRequestResolver getDelegatingResolver(ClientRegistrationRepository repo) {
        OAuth2AuthorizationRequestResolver epicResolver = epicPkceResolver(repo);
        log.debug("securityFilterChain epicResolver={}", epicResolver);
        OAuth2AuthorizationRequestResolver defaultResolver
                = new DefaultOAuth2AuthorizationRequestResolver(repo, "/oauth2/authorization");
        log.debug("securityFilterChain defaultResolver={}", defaultResolver);
        OAuth2AuthorizationRequestResolver delegatingResolver
                = new DelegatingAuthorizationRequestResolver(epicResolver, defaultResolver);
        log.debug("securityFilterChain delegatingResolver={}", delegatingResolver);
        return delegatingResolver;
    }

    /*
    ✅ This automatically:
        Generates code_verifier
        Hashes it to code_challenge
        Stores verifier in session
        Sends verifier to token endpoint    
     */
    @Bean
    public OAuth2AuthorizationRequestResolver epicPkceResolver(ClientRegistrationRepository repo) {
        DefaultOAuth2AuthorizationRequestResolver resolver
                = new DefaultOAuth2AuthorizationRequestResolver(
                        repo,
                        "/oauth2/authorization"
                );

        resolver.setAuthorizationRequestCustomizer(builder -> {
            String registrationId = (String) builder.build().getAttribute(OAuth2ParameterNames.REGISTRATION_ID);
            log.debug("epicPkceResolver registrationId={}", registrationId);
            if ("epicfhir".equals(registrationId)) {
                // ✅ Epic-required aud parameter
                // Generate PKCE code verifier and challenge
                try {
                    String codeVerifier = pkceUtil.generateCodeVerifier();
                    String codeChallenge = pkceUtil.generateCodeChallenge(codeVerifier);
                    log.debug("setAuthorizationRequestCustomizer codeVerifier={}, codeChallenge={}", codeVerifier, codeChallenge);

                    // Add PKCE parameters to authorization request
                    builder.attributes(attrs -> {
                        attrs.put(PkceParameterNames.CODE_VERIFIER, codeVerifier);
                        attrs.put("registration_id", "epicfhir");
                    }).additionalParameters(params -> {
                        params.put(PkceParameterNames.CODE_CHALLENGE, codeChallenge);
                        params.put(PkceParameterNames.CODE_CHALLENGE_METHOD, "S256");
                        params.put("aud", fhirServerUrl);
                        // SMART on FHIR specific parameters
                        params.put("response_type", "code");
                        //params.put("launch", "");
                    });
                } catch (NoSuchAlgorithmException ex) {
                    log.error("Error setAuthorizationRequestCustomizer registrationId={}", registrationId, ex);
                }

                // ✅ PKCE required by Epic SMART on FHIR
                OAuth2AuthorizationRequestCustomizers.withPkce().accept(builder);
            }
        });

        log.debug("epicPkceResolver resolver={}", resolver);
        return resolver;
    }

    @Bean
    public OAuth2AuthorizationRequestResolver defaultResolver(ClientRegistrationRepository repo) {
        log.debug("defaultResolver ClientRegistrationRepository={}", repo);
        return new DefaultOAuth2AuthorizationRequestResolver(
                repo,
                "/oauth2/authorization"
        );
    }

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
 /*
    OAuth 2.0 + PKCE in Spring Boot 3.5.6
    What PKCE Solves (1-minute explanation): PKCE protects the Authorization Code flow from interception attacks by adding:
        a code_verifier (secret)
        a code_challenge (derived from verifier)
    This is mandatory for:
        Public clients (SPA, mobile)
        SMART on FHIR healthcare integrations    
    
    When You Actually Need PKCE: Healthcare OAuth expects PKCE.
        Client Type                 PKCE Required
        SPA (React, Angular)        ✅ Yes
        Mobile App                  ✅ Yes
        Backend → Backend           ❌ No
        SMART on FHIR               ✅ Yes

    High-Level Flow (with PKCE)
    Client
      → /authorize (code_challenge)
      ← authorization_code
      → /token (code_verifier)
      ← access_token    
    
    5️⃣ Verify PKCE Is Working
        Authorization Request Should Contain:
            code_challenge=xxxx
            code_challenge_method=S256

        Token Request Should Contain:
            code_verifier=xxxx    
    
    6️⃣ Common PKCE Mistakes (Interview Gold)
        Mistake                     Result
        Sending client_secret       ❌ Token rejected
        Missing PKCE resolver       ❌ Authorization fails
        Using confidential client	❌ PKCE ignored
        Wrong redirect URI          ❌ Invalid grant
        HTTP instead of HTTPS       ❌ Blocked in prod    
    
    7️⃣ SMART on FHIR Considerations (Healthcare)
        If integrating with FHIR servers:
            PKCE is mandatory

            Scopes look like:
                launch/patient
                patient/*.read
                openid
                fhirUser

            OAuth server must support:
                PKCE
                JWT tokens
                Fine-grained scopes
    FHIR is standardized via HL7 FHIR.   
    
    9️⃣ How to Explain This in an Interview (30 seconds)
        “I enabled PKCE by switching to a public OAuth client, removing the client secret, and configuring Spring Security’s authorization request
            resolver with PKCE support. This ensures authorization codes can’t be intercepted, which is mandatory for SMART on FHIR and healthcare integrations.”

    ✅ Summary
        ✔ No client secret
        ✔ Authorization Code + PKCE
        ✔ OAuth2AuthorizationRequestCustomizers.withPkce()
        ✔ Mandatory for healthcare
        ✔ Spring Boot 3.5.6 fully supported    
    
    ✅ The Correct Mental Model
        Provider        Client Type     PKCE                    Client Secret
        SMART on FHIR   Public          Required                ❌ Not allowed
        Google          Confidential    Optional (recommended)	✅ Required
        GitHub          Confidential	Optional (recommended)  ✅ Required

        SMART on FHIR follows healthcare security standards, not social login conventions.    
    
    Why SMART on FHIR Is Different
        SMART on FHIR (built on HL7 FHIR) mandates:
            Authorization Code flow
            PKCE
            Public clients
            No client secrets
            Fine-grained scopes (patient/*.read, launch/*)
    Google and GitHub were designed for server-side web apps first.    
    
    ✅ Final Checklist (Epic on FHIR)
        ✔ Registered in Epic App Orchard
        ✔ Public client
        ✔ PKCE enabled
        ✔ No client secret
        ✔ SMART scopes
        ✔ Launch context handled
        ✔ Patient-scoped access    
    
 */
