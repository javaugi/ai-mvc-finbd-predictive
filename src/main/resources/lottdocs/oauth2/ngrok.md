Download and install ngrok on windows
H7XCTTN2Q2
3MYTX989ZS
84ZNUETTP3
E9S4TJ45G2
9W2DXUNWJ2
FS9B79NMFG
MRG5JZAWVN
MZQCDTHM9N
CPPX5DGN63
6QP6GFPJN9

(1) cofigure and run or create ngrok.yml file at Windows: "%HOMEPATH%\AppData\Local\ngrok\ngrok.yml"
ngrok config add-authtoken 372G2KQI8a23N27pb1aHxFolEEu_2KWJLhiiwPVsUj2k7hRDx

"%HOMEPATH%\AppData\Local\ngrok\ngrok.yml"
---
version: "2"
authtoken: 372G2KQI8a23N27pb1aHxFolEEu_2KWJLhiiwPVsUj2k7hRDx

tunnels:
  web-app:
    proto: http
    addr: 8088
    host_header: "rewrite" # Useful for some local development setups

  postgres-db:
    proto: tcp
    addr: 5432
    # Use 'remote_addr' for a reserved public TCP address (requires a paid plan)
    # remote_addr: 2.tcp.ngrok.io:21746    
---

(2) start at Windows Powershell
ngrok http 8088
The output:
Session Status                online
Account                       david.lee.remax@gmail.com (Plan: Free)                                            
Update                        update available (version 3.34.1, Ctrl-U to update)
Version                       3.24.0-msix
Region                        United States (us)
Latency                       86ms                                                                                                                    
Web Interface                 http://127.0.0.1:4040
Forwarding                    https://contractile-unruefully-madilynn.ngrok-free.dev -> http://localhost:8088
Connections                   ttl     opn     rt1     rt5     p50     p90
                              0       0       0.00    0.00    0.00    0.00
(3) add https://contractile-unruefully-madilynn.ngrok-free.dev to https://fhir.epic.com/Developer/Edit?appId=49023


You’re very close — your redirect URI, PKCE, aud, and iss are all correct.
What your logs + ngrok trace show tells us exactly where it’s failing.

This is not an ngrok or redirect-URI mismatch problem anymore.

✅ What Is Working (Important)
From your log:

Redirecting to https://fhir.epic.com/.../authorize
  response_type=code
  client_id=...
  redirect_uri=https://contractile-unruefully-madilynn.ngrok-free.dev/login/oauth2/code/epicfhir
  code_challenge=...
  aud=https://fhir.epic.com/.../FHIR/R4
  iss=https://fhir.epic.com/.../FHIR/R4

.SecurityConfig - epicPkceResolver registrationId=epicfhir
17:05:47.952 [http-nio-8088-exec-4] DEBUG o.s.s.web.DefaultRedirectStrategy - Redirecting to 
https://fhir.epic.com/interconnect-fhir-oauth/oauth2/authorize
?response_type=code
&client_id=2a341264-75bd-49b3-8b12-609c09eca2f5
&scope=openid%20fhirUser%20patient/*.read%20launch/patient
&state=-2mpxg9iBX_jX3V8G1GB3QjagJ7c8G6QplfxU3xjYnY%3D
&redirect_uri=https://contractile-unruefully-madilynn.ngrok-free.dev/login/oauth2/code/epicfhir&nonce=SX6ZufNoExv9J4X624V9qrOatwPJYOLyzV4fa5lhRGQ
&code_challenge=N35sx6GqFmhiOn5jEKDI4iTarERGcBSJrlACzCoMAts
&code_challenge_method=S256
&aud=https://fhir.epic.com/interconnect-fhir-oauth/api/FHIR/R4
&iss=https://fhir.epic.com/interconnect-fhir-oauth/api/FHIR/R4

Redirecting to 
https://fhir.epic.com/interconnect-fhir-oauth/oauth2/authorize
?response_type=code
&client_id=2a341264-75bd-49b3-8b12-609c09eca2f5&scope=openid%20fhirUser%20patient/*.read%20launch/patient
&state=6Iq4OR6Wgmn7ew9I_mzTCc4yk4-Wn6ZIdM7jfNb1Bcc%3D
&redirect_uri=https://contractile-unruefully-madilynn.ngrok-free.dev/login/oauth2/code/epicfhir
&nonce=dTZ_fTTiKct5MV6f_DHUPLcPDF0WbRlkd44LesrvZL0
&code_challenge=TKzs8S3g5-FVh8ULY_uGn6WruDzksXlL6IY_bkn-I6Y
&code_challenge_method=S256
&aud=https://fhir.epic.com/interconnect-fhir-oauth/api/FHIR/R4
&iss=https://fhir.epic.com/interconnect-fhir-oauth/api/FHIR/R4


✔ PKCE present
✔ aud and iss present
✔ Redirect URI EXACT match
✔ ngrok HTTPS live
✔ Epic authorize endpoint reached

So Epic accepts the authorize request.

🔴 Where It Is Failing

From ngrok:

HTTP Requests
-------------
GET /   401


⚠️ Epic is redirecting back to your app, but Spring Security is rejecting the callback.

That means:

❌ The authorization code → token exchange is failing

🎯 Root Cause (99% certain)
❌ You are missing client-authentication = NONE

SMART-on-FHIR requires public clients:
    NO client secret
    PKCE only
    Spring Boot defaults to:
    client_secret_basic

Epic rejects that silently → generic OAuth error.

✅ REQUIRED FIX (This Is the Key)
Update Epic client registration
spring:
  security:
    oauth2:
      client:
        registration:
          epicfhir:
            client-id: 2a341264-75bd-49b3-8b12-609c09eca2f5
            authorization-grant-type: authorization_code
            client-authentication-method: none   # 🔥 REQUIRED
            redirect-uri: "{baseUrl}/login/oauth2/code/{registrationId}"
            scope:
              - openid
              - fhirUser
              - patient/*.read
              - launch/patient
              
⛔ Do NOT set client-secret
⛔ Do NOT use client_secret_basic

✅ Why Google/GitHub Work but Epic Fails
Provider	Client Type     Secret
Google      Confidential	Yes
GitHub      Confidential	Yes
Epic SMART	Public          NO

Spring Security needs this explicit override for Epic.
🧪 How to Verify Immediately
After restart, watch for this request hitting ngrok:
    POST /login/oauth2/code/epicfhir

🏥 Epic-Specific Reality Check

Epic Systems - Epic’s SMART-on-FHIR implementation:
    Rejects client secrets for public apps
    Requires PKCE
    Fails token exchange without clear UI error

This behavior is expected and infamous among Epic integrators.

✅ Final Checklist
✔ HTTPS redirect (ngrok)
✔ PKCE enabled only for Epic
✔ aud + iss
✔ client-authentication-method: none ← THIS WAS THE BLOCKER