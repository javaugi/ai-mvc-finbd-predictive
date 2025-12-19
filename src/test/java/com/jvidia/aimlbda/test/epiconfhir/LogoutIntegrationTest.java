/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.test.epiconfhir;

import com.jvidia.aimlbda.MyApplicationBaseTests;
import static org.hamcrest.CoreMatchers.containsString;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


/*
4️⃣ Integration Test — Logout Flow (Your Recent Bug)

This ensures:
    /auth/logout works
    /login is never hit
    Redirect goes to SPA

5️⃣ What you CANNOT realistically test (and shouldn’t)
    ❌ Live Epic OAuth servers
    ❌ Token exchange against Epic
    ❌ Patient FHIR resources without sandbox credentials

👉 Epic explicitly discourages automated tests against production OAuth endpoints.

6️⃣ What Epic & Interviewers LOVE to hear
    “We unit-test PKCE generation and SMART parameters locally, integration-test the authorization redirect, and mock
        Epic endpoints. Live OAuth is validated manually against Epic sandbox environments.”

 */
public class LogoutIntegrationTest extends MyApplicationBaseTests {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void logoutShouldRedirectToSpaLogin() throws Exception {
        mockMvc.perform(post("/auth/logout")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string(
                        "Location",
                        containsString("localhost:3000/login")
                ));
    }
}
