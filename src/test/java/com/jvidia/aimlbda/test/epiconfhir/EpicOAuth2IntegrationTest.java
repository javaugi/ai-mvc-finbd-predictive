/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.test.epiconfhir;

import com.jvidia.aimlbda.MyApplicationBaseTests;
import static org.hamcrest.CoreMatchers.containsString;
import org.junit.jupiter.api.Test;
//import static org.slf4j.MDC.get;
import org.springframework.beans.factory.annotation.Autowired;
//import static org.springframework.javapoet.TypeVariableName.get;
//import static org.springframework.javapoet.TypeVariableName.get;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

/*
3️⃣ Integration Test — OAuth2 Authorization Endpoint (NO Epic)

This validates:
    Security filter chain
    Authorization endpoint wiring
    No /login regression

📌 This is the most valuable integration test

✔️ Confirms OAuth2 flow starts
✔️ Confirms Epic endpoint is used
✔️ Confirms security chain is wired correctly    
 */
public class EpicOAuth2IntegrationTest extends MyApplicationBaseTests {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void oauth2AuthorizationEndpointShouldRedirect() throws Exception {
        mockMvc.perform(get("/oauth2/authorization/epicfhir"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location",
                        containsString("fhir.epic.com")));
    }
}
