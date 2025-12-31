Setup Instructions
Backend Setup: Create a new Spring Boot project with the above files

Set up OAuth2 credentials:
    Google: Go to Google Cloud Console https://console.cloud.google.com/
        (1)  https://console.cloud.google.com/auth/overview?project=favorable-iris-460920-u2  Cloud Auth Platform
        (2) clients -> create cliet -> name: WebClientRemax101
        (3) Authorized JavaScript origins: http://localhost:5173
        (4) Authorized redirect URIs: http://localhost:8088/login/oauth2/code/google, http://localhost:5173
        Client ID: 879160345815-8q1q91mv1dbgajpf7qimm0fuvsdl0j7q.apps.googleusercontent.com
        (5) Create credentials APIkeyRemax101: AIzaSyBS5eh60RFkjglZh-50r-sUIzYjT9J9jmo

    Create credentials (OAuth 2.0 Client IDs)
    Add http://localhost:8088/login/oauth2/code/google as authorized redirect URI

GitHub: Go to GitHub Settings → Developer settings → OAuth Apps
    Create new OAuth App
    Add http://localhost:8088/login/oauth2/code/github as authorization callback URL
    Update application.yml with your actual client IDs and secrets
    (1) Register a new OAuth app Application Name: WebClientRemax101Github
    (2) Homepage URL:  http://localhost:5173/home
    (3) Application description: ai-mvc-finbda-predicta
    (4) Authorization callback URL: http://localhost:8088/login/oauth2/code/github
    (5) personal access token ntoke042225: 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

EPIC FHIR https://fhir.epic.com/Developer/Index
    (1) sign up  david.lee.remax@gmail.com
    (2) usernme/pwd dlee9591jugi/JiaxianHmeEOF1@8
    (3) Create my first app:
        (a) App Name: WebEpicOnFhir101
        (b) Application Audience: 
                Patients 
                Clinicians or Administrative Users (This one)
                Backend Systems
        (c) Redirect URI: 
                localhost:5173/callback
                localhost:4200/callback
                localhost:3000/callback
    (4) Client Id: 2a341264-75bd-49b3-8b12-609c09eca2f5 
        Non-Production Client Id:1a95d991-b937-42a1-bb91-6182a33fa5f4

    To mark an application as a Standalone Launch on the Epic developer platform (often referred to as Epic on FHIR or App Orchard), 
        you need to configure the launch settings in your application record.
    
    "Standalone Launch" refers to the SMART on FHIR flow where a user opens your app directly (e.g., from a bookmark or mobile app)
        and then logs into Epic, as opposed to an "EHR Launch" where the app is opened from within Hyperspace.
    Steps to Enable Standalone Launch
        1 Log in to the Portal:Sign in to the Epic on FHIR / MyApps portal.
        2 Select Your Application:Navigate to your existing application record.
        3 Update Application Settings: Look for a section typically labeled Launch Settings or SMART Settings.
            Enable Standalone Launch: There is usually a checkbox or selection menu to indicate that the application supports the 
                "Standalone Launch" workflow.
            Configure Redirect URIs: Ensure you have provided a valid Redirect URI. For a standalone launch, Epic will redirect the user 
                back to this URL after they have successfully authenticated and selected a patient.
        4. Save & Ready for Production: Once you have toggled this setting, click Save. Note that if you intend to use this in a 
            production environment, you must click the "Save & Ready for Production" button to trigger the distribution of your Client 
            ID to actual health systems.
    
Key Technical Implementation
    Marking it in the portal is only half the battle; your code must also be prepared to handle the standalone flow.
    No Launch Token: Unlike an EHR launch, a standalone launch will not provide a launch parameter in the URL.
    The "ISS" Parameter: Your app should be able to receive an iss parameter (the FHIR server URL) or allow the user to select their healthcare provider.

OAuth Request: Your application must initiate the OAuth 2.0 flow by hitting the Epic authorize endpoint:
https://fhir.epic.com/interconnect-fhir-oauth/oauth2/authorize?
response_type=code&
client_id=[your_client_id]&
redirect_uri=[your_redirect_uri]&
state=[unique_state]&
aud=[fhir_base_url]

Summary of Launch Contexts
    Launch Type     Triggered From              Required Parameters
    EHR Launch      Inside Epic (Hyperspace)    iss, launch
    Standalone      External(Web/Mobile)        iss (or user selects it)

Frontend Setup:
    Create a new React app and replace the files with the above
    Run npm install to install dependencies
    Start the frontend with npm start

Running the Application:
    Start the Spring Boot backend (runs on port 8088)
    Start the React frontend (runs on port 3000)
    Access the application at http://localhost:3000

Features Included:
    ✅ Google OAuth2 login
    ✅ GitHub OAuth2 login
    ✅ Internal login form (placeholder)
    ✅ Dashboard with user info
    ✅ Links to Actuator endpoints
    ✅ Link to Swagger UI
    ✅ Proper CORS configuration
    ✅ Session management
    ✅ Logout functionality
The application provides a complete OAuth2 authentication flow with a professional-looking React frontend and fully functional Spring Boot backend.
####### Google Login steps
1. Click "Login with Google" at http://localhost:5173/login
2. Choose a Google account: -> click david.lee.remax@gmail.com
3. SecurityConfig.securityFilterChain
4. security.web.FilterChainProxy - Securing GET /oauth2/authorization/google?redirect_uri=http://localhost:5173
5. web.DefaultRedirectStrategy - Redirecting to https://accounts.google.com/o/oauth2/auth
    ?response_type=code
    &client_id=879160345815-8q1q91mv1dbgajpf7qimm0fuvsdl0j7q.apps.googleusercontent.com
    &scope=profile%20email
    &state=aHGqwDYD1dKiJTwyh0gkk827CG_SDgiC9wqHyzqiHIw%3D
    &redirect_uri=http://localhost:8088/login/oauth2/code/google
6. security.web.FilterChainProxy - Securing GET /login/oauth2/code/google
    ?state=aHGqwDYD1dKiJTwyh0gkk827CG_SDgiC9wqHyzqiHIw%3D
    &code=4%2F0Ab32j90AknnNPQniMYaeuJurRLD1OQJJAchDVFIvW8hCpoRzDzYP01v--uYbgq6KxIShIQ
    &scope=email+profile+https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.profile+https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email+openid
    &authuser=0&prompt=none
7.  web.client.RestTemplate - HTTP POST https://oauth2.googleapis.com/token
    web.client.RestTemplate - Accept=[application/json, application/*+json]
    web.client.RestTemplate - Writing [{grant_type=[authorization_code], code=[4/0Ab32j90AknnNPQniMYaeuJurRLD1OQJJAchDVFIvW8hCpoRzDzYP01v--uYbgq6KxIShIQ], 
            redirect_uri=[http://localhost:8088/login/oauth2/code/google]}] as "application/x-www-form-urlencoded;charset=UTF-8"
    web.client.RestTemplate - Response 200 OK
    web.client.RestTemplate - Reading to [org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse] as "application/json;charset=utf-8"
    web.client.RestTemplate - HTTP GET https://www.googleapis.com/oauth2/v3/userinfo
    web.client.RestTemplate - Accept=[application/json, application/yaml, application/*+json]
    web.client.RestTemplate - Response 200 OK
    web.client.RestTemplate - Reading to [java.util.Map<java.lang.String, java.lang.Object>]
8.  HttpSessionSecurityContextRepository - Stored SecurityContextImpl [Authentication=OAuth2AuthenticationToken [Principal=Name: [108103292630000776130], 
        Granted Authorities: [[OAUTH2_USER, SCOPE_https://www.googleapis.com/auth/userinfo.email, SCOPE_https://www.googleapis.com/auth/userinfo.profile, 
        SCOPE_openid]], User Attributes: [{sub=108103292630000776130, name=David Lee, given_name=David, family_name=Lee, 
        picture=https://lh3.googleusercontent.com/a-/ALV-Uj ...
9.  OAuth2LoginAuthenticationFilter - Set SecurityContextHolder to OAuth2AuthenticationToken [Principal=Name: [108103292630000776130], 
        Granted Authorities: [[OAUTH2_USER, SCOPE_https://www.googleapis.com/auth/userinfo.email, SCOPE_https://www.googleapis.com/auth/userinfo.profile, 
        SCOPE_openid]], User Attributes: [{sub=108103292630000776130, name=David Lee, given_name=David, family_name=Lee, 
        picture=https://lh3.googleusercontent.com/a-/ALV-Uj
10. OAuth2LoginSuccessHandler - onAuthenticationSuccess 
        email   : david.lee.remax@gmail.com 
        user Name: [108103292630000776130], 
        Granted Authorities: [[OAUTH2_USER, SCOPE_https://www.googleapis.com/auth/userinfo.email, SCOPE_https://www.googleapis.com/auth/userinfo.profile, 
        SCOPE_openid]], User Attributes: [{sub=108103292630000776130, name=David Lee, given_name=David, family_name=Lee, 
        picture=https://lh3.googleusercontent.com/a-/ALV-Uj
11. OAuth2LoginSuccessHandler - onAuthenticationSuccess savedUser UserInfo(id=2, name=David Lee, email=david.lee.remax@gmail.com, 
        password=JiaxianHomeRem1@8@9, imageUrl=https://lh3.googleusercontent.com/a-/ALV-Uj
12. aimlbda.service.JwtTokenService - createToken username david.lee.remax@gmail.com roles [ROLE_USER]
    OAuth2LoginSuccessHandler - determineTargetUrl redirectUri true
    OAuth2LoginSuccessHandler - onAuthenticationSuccess 
        token eyJhbGciOiJIUzUxMiJ9.eyJyb2xlcyI6W3siYXV0aG9yaXR5IjoiUk9MRV9VU0VSIn1dLCJzdWIiOiJkYXZpZC5sZWUucmVtYXhAZ21haWwuY29tIiwiaWF0IjoxNzYyNjIyNTU
            3LCJleHAiOjE3NjI2MjI1NTd9.Nwv9lvyiRmVDOD7qVlgKuLhjdJVfCeemafOGn6oRyB2kmFY1TMBjzcFH4N49eXLfHnTqVie-7Kf0deDRpaoLXw 
        targetUrl http://localhost:5173?token=eyJhbGciOiJIUzUxMiJ9.eyJyb2xlcyI6W3siYXV0aG9yaXR5IjoiUk9MRV9VU0VSIn1dLCJzdWIiOiJkYXZpZC5sZWUucmVtYXhAZ21ha
            WwuY29tIiwiaWF0IjoxNzYyNjIyNTU3LCJleHAiOjE3NjI2MjI1NTd9.Nwv9lvyiRmVDOD7qVlgKuLhjdJVfCeemafOGn6oRyB2kmFY1TMBjzcFH4N49eXLfHnTqVie-7Kf0deDRpaoLXw
    web.DefaultRedirectStrategy - Redirecting to http://localhost:5173?token=eyJhbGciOiJIUzUxMiJ9.eyJyb2xlcyI6W3siYXV0aG9yaXR5IjoiUk9MRV9VU0VSIn1dLCJzdW
        IiOiJkYXZpZC5sZWUucmVtYXhAZ21haWwuY29tIiwiaWF0IjoxNzYyNjIyNTU3LCJleHAiOjE3NjI2MjI1NTd9.Nwv9lvyiRmVDOD7qVlgKuLhjdJVfCeemafOGn6oRyB2kmFY1TMBjzcFH4
        N49eXLfHnTqVie-7Kf0deDRpaoLXw
################################### the above is the full logon process and below is the swagger-ui/index.html
    security.web.FilterChainProxy - Securing GET /swagger-ui/index.html
12. JwtAuthenticationFilter - doFilterInternal token null
    security.web.FilterChainProxy - Secured GET /swagger-ui/index.html
    web.servlet.DispatcherServlet - GET "/swagger-ui/index.html", parameters={}
    SimpleUrlHandlerMapping - Mapped to ResourceHttpRequestHandler [classpath [META-INF/resources/webjars/]]
    web.servlet.DispatcherServlet - Completed 200 OK
13. HttpSessionSecurityContextRepository - Retrieved SecurityContextImpl [Authentication=OAuth2AuthenticationToken [Principal=Name: [108103292630000776130], 
        Granted Authorities: [[OAUTH2_USER, SCOPE_https://www.googleapis.com/auth/userinfo.email, SCOPE_https://www.googleapis.com/auth/userinfo.profile, 
    SCOPE_openid]], User Attributes: [{sub=108103292630000776130, name=David Lee, given_name=David, family_name=Lee, 
    picture=https://lh3.googleusercontent.com/a-/ALV-Uj..., email=david.lee.remax@gmail.com, email_verified=true}], Credentials=[PROTECTED], 
    Authenticated=true, Details=WebAuthenticationDetails [RemoteIpAddress=0:0:0:0:0:0:0:1, SessionId=null], Granted Authorities=[OAUTH2_USER, 
    SCOPE_https://www.googleapis.com/auth/userinfo.email, SCOPE_https://www.googleapis.com/auth/userinfo.profile, SCOPE_openid]]]

################ Github Logon steps
1. Click "Login with Github" at http://localhost:5173/login
2. web.servlet.DispatcherServlet - Initializing Servlet 'dispatcherServlet'
    web.servlet.DispatcherServlet - Detected StandardServletMultipartResolver
    web.servlet.DispatcherServlet - Detected AcceptHeaderLocaleResolver
    web.servlet.DispatcherServlet - Detected FixedThemeResolver
    RequestMappingHandlerAdapter - ControllerAdvice beans: 0 @ModelAttribute, 0 @InitBinder, 1 RequestBodyAdvice, 1 ResponseBodyAdvice
    DispatcherServlet - Detected org.springframework.web.servlet.view.DefaultRequestToViewNameTranslator@681c89eb
    web.servlet.DispatcherServlet - Detected org.springframework.web.servlet.support.SessionFlashMapManager@320ef1a3
    web.servlet.DispatcherServlet - enableLoggingRequestDetails='false': request parameters and headers will be masked to prevent unsafe logging 
        of potentially sensitive data
    web.servlet.DispatcherServlet - Completed initialization in 165 ms
    InitializeAuthenticationProviderBeanManagerConfigurer$InitializeAuthenticationProviderManagerConfigurer - Global AuthenticationManager 
        configured with AuthenticationProvider bean with name authenticationProvider
    InitializeUserDetailsBeanManagerConfigurer$InitializeUserDetailsManagerConfigurer - Global AuthenticationManager configured with an 
        AuthenticationProvider bean. UserDetailsService beans will not be used by Spring Security for automatically configuring username/password 
        login. Consider removing the AuthenticationProvider bean. Alternatively, consider using the UserDetailsService in a manually instantiated 
        DaoAuthenticationProvider. If the current configuration is intentional, to turn off this warning, increase the logging level of 
        'org.springframework.security.config.annotation.authentication.configuration.InitializeUserDetailsBeanManagerConfigurer' to ERROR
3. SecurityConfig.securityFilterChain - securityFilterChain .....
    web.DefaultSecurityFilterChain - Will secure any request with filters: DisableEncodeUrlFilter, WebAsyncManagerIntegrationFilter, 
        SecurityContextHolderFilter, HeaderWriterFilter, CorsFilter, LogoutFilter, OAuth2AuthorizationRequestRedirectFilter, 
        OAuth2LoginAuthenticationFilter, JwtAuthenticationFilter, RequestCacheAwareFilter, SecurityContextHolderAwareRequestFilter,
        AnonymousAuthenticationFilter, SessionManagementFilter, ExceptionTranslationFilter, AuthorizationFilter
    security.web.FilterChainProxy - Securing GET /oauth2/authorization/github?redirect_uri=http://localhost:5173
    web.DefaultRedirectStrategy - Redirecting to https://github.com/login/oauth/authorize
        ?response_type=code
        &client_id=Ov23lifuH3w20TeTxaZJ
        &scope=user:email%20read:user&state=rgjbjg3BHfP7A70gGvIxOrnH3gQoKsYKuQz0U4ROs7g%3D
        &redirect_uri=http://localhost:8088/login/oauth2/code/github
    security.web.FilterChainProxy - Securing GET /login/oauth2/code/github?code=89f4d514f6ea6108e951&state=rgjbjg3BHfP7A70gGvIxOrnH3gQoKsYKuQz0U4ROs7g%3D
    web.client.RestTemplate - HTTP POST https://github.com/login/oauth/access_token
    web.client.RestTemplate - Accept=[application/json, application/*+json]
    web.client.RestTemplate - Writing [{grant_type=[authorization_code], code=[89f4d514f6ea6108e951], 
        redirect_uri=[http://localhost:8088/login/oauth2/code/github]}] as "application/x-www-form-urlencoded;charset=UTF-8"
    web.client.RestTemplate - Response 200 OK
    web.client.RestTemplate - Reading to [org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse] as "application/json;charset=utf-8"
    web.client.RestTemplate - HTTP GET https://api.github.com/user
    web.client.RestTemplate - Accept=[application/json, application/yaml, application/*+json]
    web.client.RestTemplate - Response 200 OK
    web.client.RestTemplate - Reading to [java.util.Map<java.lang.String, java.lang.Object>]
    DefaultAuthenticationEventPublisher - No event was found for the exception org.springframework.security.authentication
        .InternalAuthenticationServiceException
    authentication.ProviderManager - Authentication service failed internally for user ''
    security.authentication.InternalAuthenticationServiceException: Cannot invoke "String.isEmpty()" because the return value of 
        "com.jvidia.aimlbda.security.oauth2.user.OAuth2UserInfo.getEmail()" is null
        at com.jvidia.aimlbda.service.OAuthUserService.loadUser(OAuthUserService.java:45)
    NullPointerException: Cannot invoke "String.isEmpty()" because the return value of "com.jvidia.aimlbda.security.oauth2.user.OAuth2UserInfo.getEmail()"  
        is null at com.jvidia.aimlbda.service.OAuthUserService.processOAuth2User(OAuthUserService.java:53)
        at com.jvidia.aimlbda.service.OAuthUserService.loadUser(OAuthUserService.java:43)
        ... 98 common frames omitted
    OAuth2LoginAuthenticationFilter - An internal error occurred while trying to authenticate the user.
    security.authentication.InternalAuthenticationServiceException: Cannot invoke "String.isEmpty()" because the return value of 
        "com.jvidia.aimlbda.security.oauth2.user.OAuth2UserInfo.getEmail()" is null
        at com.jvidia.aimlbda.service.OAuthUserService.loadUser(OAuthUserService.java:45)
    DefaultRedirectStrategy - Redirecting to /login?error
    security.web.FilterChainProxy - Securing GET /login?error
    JwtAuthenticationFilter - doFilterInternal token null
    AnonymousAuthenticationFilter - Set SecurityContextHolder to anonymous SecurityContext
    HttpSessionRequestCache - Saved request http://localhost:8088/login?error&continue to session
    ExceptionHandlerExceptionResolver - Using @ExceptionHandler com.jvidia.aimlbda.security.exception.CustomExceptionHandler
        #handleAuthenticationException(Exception)
    HttpEntityMethodProcessor - Using 'application/json;q=0.8', given [text/html, application/xhtml+xml, image/avif, image/webp, 
        image/apng, application/xml;q=0.9, */*;q=0.8, application/signed-exchange;v=b3;q=0.7] and supported [application/json, application/*+json, 
        application/yaml]
    HttpEntityMethodProcessor - Writing [ErrorResponse{status=401 UNAUTHORIZED, headers=[], body=ProblemDetail[type='about:blank', title='Una (truncated)...]
    ExceptionHandlerExceptionResolver - Resolved [org.springframework.security.authentication.InsufficientAuthenticationException: Full authentication 
        is required to access this resource]
    security.web.FilterChainProxy - Securing GET /favicon.ico
    JwtAuthenticationFilter - doFilterInternal token null
    AnonymousAuthenticationFilter - Set SecurityContextHolder to anonymous SecurityContext
    ExceptionHandlerExceptionResolver - Using @ExceptionHandler com.jvidia.aimlbda.security.exception.CustomExceptionHandler
        #handleAuthenticationException(Exception)
    HttpEntityMethodProcessor - Using 'application/json;q=0.8', given [image/avif, image/webp, image/apng, image/svg+xml, image/*, */*;q=0.8] 
        and supported [application/json, application/*+json, application/yaml]
    HttpEntityMethodProcessor - Writing [ErrorResponse{status=401 UNAUTHORIZED, headers=[], body=ProblemDetail[type='about:blank', title='Una (truncated)...]
    ExceptionHandlerExceptionResolver - Resolved [org.springframework.security.authentication.InsufficientAuthenticationException: 
        Full authentication is required to access this resource]

################ Github Logon

###### new error
http://localhost:8088/login/oauth2/code/google
?state=ohq2Recb0KT4sHNOWS-XKDVhM3fwHNgIvPCQNPB9ZiY%3D&code=4%2F0Ab32j91Dxe0PuI4SDbWTjX7yTPS_KtlnGz6VzijPvSKZV3qREe94rca5H6KPw22TZQR2Yw
&scope=email+profile+https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.profile+https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email+openid
&authuser=0&prompt=consent

#######
https://accounts.google.com/o/oauth2/auth?response_type=code
&client_id=david.lee.remax@gmail.com
&scope=profile%20email
&state=...
&redirect_uri=http://localhost:8088/login/oauth2/code/google


DefaultSecurityFilterChain - Will secure any request with filters: DisableEncodeUrlFilter, 
WebAsyncManagerIntegrationFilter, SecurityContextHolderFilter, HeaderWriterFilter, CorsFilter, 
LogoutFilter, OAuth2AuthorizationRequestRedirectFilter, OAuth2LoginAuthenticationFilter, 
JwtAuthenticationFilter, RequestCacheAwareFilter, SecurityContextHolderAwareRequestFilter, 
AnonymousAuthenticationFilter, SessionManagementFilter, ExceptionTranslationFilter, AuthorizationFilter

FilterChainProxy - Securing GET /oauth2/authorization/google?redirect_uri=http://localhost:5173
DefaultRedirectStrategy - Redirecting to 
https://accounts.google.com/o/oauth2/auth?response_type=code
&client_id=david.lee.remax@gmail.com
&scope=profile%20email
&state=27y1WtjgzCTXajh5I6YA-Ef5v8sVf0XhgEjcgd7rwNw%3D
&redirect_uri=http://localhost:8088/login/oauth2/code/google

##########
Perfect — this log trace tells me exactly what’s happening. 👍
You’re hitting the Spring Security OAuth2 login endpoint for Google, but Google is rejecting the redirect URL with a 
    generic 400 (“malformed request”) before even reaching your app.
Let’s decode what’s going on and fix it properly.
########
🧩 Root Cause
From your log:
    GET /oauth2/authorization/google?redirect_uri=http://localhost:5173
    Spring is redirecting to:
        https://accounts.google.com/o/oauth2/auth?response_type=code
        &client_id=david.lee.remax@gmail.com
        &scope=profile%20email
        &state=...
        &redirect_uri=http://localhost:8088/login/oauth2/code/google
And you get:
    400. That’s an error.
    The server cannot process the request because it is malformed.
✅ So Google received a valid OAuth2 request, but it rejected it because the redirect URI didn’t match what’s registered in the Google Cloud Console.
#######
🧭 What’s Happening
Spring Security builds the redirect URL automatically using this format:
    {baseUrl}/login/oauth2/code/{registrationId}
In your case:
    http://localhost:8088/login/oauth2/code/google

Google only allows redirect URIs exactly matching the ones configured in your Google API Console → OAuth 2.0 Client ID settings.
    If you registered something like:
        http://localhost:5173
    or
        ``://localhost:8080/login/oauth2/code/google`
    then `http://localhost:8088/login/oauth2/code/google` will not match — Google rejects it with a **400**.
---
#######
## 🩹 How to Fix
### ✅ Step 1. Go to Google Cloud Console
- Navigate to:  
  [https://console.cloud.google.com/apis/credentials](https://console.cloud.google.com/apis/credentials)
- Select your **OAuth 2.0 Client ID**
- Under **Authorized redirect URIs**, add:
    http://localhost:8088/login/oauth2/code/google
        (or whatever port your Spring Boot app is running on).
    You can add multiple entries if needed:
        http://localhost:8080/login/oauth2/code/google
        http://localhost:8088/login/oauth2/code/google
        https://yourdomain.com/login/oauth2/code/google

Then **save** the configuration.
#########
### ✅ Step 2. Check `application.yml` (or `application.properties`)
Make sure your configuration matches that same URL.
    **Example (`application.yml`):**
    ```yaml
    spring:
      security:
        oauth2:
          client:
            registration:
              google:
                client-id: YOUR_GOOGLE_CLIENT_ID
                client-secret: YOUR_GOOGLE_CLIENT_SECRET
                scope:
                  - profile
                  - email
            provider:
              google:
                authorization-uri: https://accounts.google.com/o/oauth2/auth
                token-uri: https://oauth2.googleapis.com/token
                user-info-uri: https://www.googleapis.com/oauth2/v3/userinfo
                user-name-attribute: sub
      web:
        base-url: http://localhost:8088
########
✅ Step 3. Restart and Retry
    Now when you visit:
        http://localhost:8088/oauth2/authorization/google
    Spring will redirect you to Google correctly and the redirect URI will match your registered entry.
###########
Optional: SPA (Frontend) Redirection

You mentioned redirect_uri=http://localhost:5173 — that’s your frontend (likely Vite/React).
If your flow is:
    React on port 5173
    Spring Boot backend on port 8088

Then the proper setup is:
    React calls your backend endpoint:
    GET http://localhost:8088/oauth2/authorization/google
    Spring redirects to Google (handles OAuth2 flow)
After successful login, Google redirects back to the backend:
    http://localhost:8088/login/oauth2/code/google

Backend exchanges the code → token, then redirects back to frontend:
    response.sendRedirect("http://localhost:5173/home");

If you want this post-login redirect to the frontend, configure:

spring:
  security:
    oauth2:
      client:
        registration:
          google:
            redirect-uri: "{baseUrl}/login/oauth2/code/{registrationId}"
      success-redirect-url: http://localhost:5173/home

Or programmatically in your OAuth2LoginSuccessHandler.

✅ TL;DR Fix Summary
    Problem                                             Fix
    400 Bad Request from Google                         Add correct redirect URI to Google Cloud Console
    Mismatch between backend port and registered URI	Use same port in both
    React frontend on port 5173                         Use backend (8088) for OAuth2 callback, then redirect manually to frontend
##################################################
app name:   MyLocalOauth2Test
app category:   External
app type:       Web Application
The name of your OAuth 2.0 client: WebClientRemax101
# . This name is only used to identify the client in the console and will not be shown to end users.

Created the following:
###
clientId:879160345815-8q1q91mv1dbgajpf7qimm0fuvsdl0j7q.apps.googleusercontent.com
client secret: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


###################################################
Perfect — you’re very close. The key here is understanding how Spring Security’s redirect flow interacts with Google’s registered redirect URI.
Let’s analyze the mismatch and fix it so your Spring Boot backend + React frontend OAuth2 flow works correctly.
🚨 The Problem
    From your debug log:
        Redirecting to https://accounts.google.com/o/oauth2/auth?...
        &redirect_uri=http://localhost:8088/login/oauth2/code/google
    But you registered http://localhost:5173 in Google Cloud Console.
Google expects the redirect URI in the OAuth request to exactly match what you registered.
❌ Mismatch:
Google expects	Spring sends
    http://localhost:5173	http://localhost:8088/login/oauth2/code/google
Hence:
    Error 400: redirect_uri_mismatch

⚙️ Why This Happens
When you hit:
    http://localhost:8088/oauth2/authorization/google?redirect_uri=http://localhost:5173
Spring Security ignores that redirect_uri query parameter by default for security reasons — it uses its own configured redirect URI template, typically:
    {baseUrl}/login/oauth2/code/{registrationId}

So even though you pass redirect_uri=http://localhost:5173, it doesn’t change Google’s redirect destination — Spring still uses its backend callback.

✅ The Correct Setup
    To fix this properly, you should register your Spring Boot backend callback URI with Google, not your React frontend.
    Then, once login succeeds, you can manually redirect from your backend → React frontend.
✅ Step 1. Update Google OAuth Client Settings
    Go to [Google Cloud Console → Credentials → OAuth 2.0 Client ID].
    Under Authorized redirect URIs, add:
        http://localhost:8088/login/oauth2/code/google

If you deploy later to production, also add:
    https://yourdomain.com/login/oauth2/code/google

👉 You can keep http://localhost:5173 for other use cases, but it won’t be used by Spring Security.

✅ Step 2. Update Spring Boot Configuration

In your application.yml (or application.properties):

spring:
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: 879160345815-8q1q91mv1dbgajpf7qimm0fuvsdl0j7q.apps.googleusercontent.com
            client-secret: YOUR_CLIENT_SECRET
            redirect-uri: "{baseUrl}/login/oauth2/code/{registrationId}"
            scope:
              - profile
              - email


Spring will then correctly match the URI you registered with Google.

✅ Step 3. Redirect Back to Frontend After Successful Login

Spring handles Google’s callback at:
    http://localhost:8088/login/oauth2/code/google
After successful authentication, you can tell Spring to redirect the user to your React app (http://localhost:5173).

You can do this in a custom success handler:

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/public/**", "/oauth2/**").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .successHandler((request, response, authentication) -> {
                    response.sendRedirect("http://localhost:5173");
                })
            );

        return http.build();
    }


That’s it — now after login, Spring handles the Google token exchange, authenticates the user, and then redirects your browser to your frontend app.

✅ Step 4. Update Your React Button
    Simplify your React code — remove the extra redirect_uri param (since Spring ignores it anyway):

    const handleGoogleLogin = () => {
      window.location.href = "http://localhost:8088/oauth2/authorization/google";
    };

✅ TL;DR Summary
    Task                            Correct Configuration
    Google redirect URI             http://localhost:8088/login/oauth2/code/google
    Spring Boot redirect template	{baseUrl}/login/oauth2/code/{registrationId}
    React login button              http://localhost:8088/oauth2/authorization/google
    Post-login redirect             Implement custom successHandler() → http://localhost:5173
Once you make these changes:
    Restart your Spring Boot app.
    Visit your React app and click “Login with Google.”
    You’ll be redirected → Google → back to Spring Boot → then to your React app.
✅ No more redirect_uri_mismatch.

Would you like me to show a complete working backend config (Spring Boot SecurityConfig + application.yml) that includes this success handler 
    redirect flow for your React frontend?