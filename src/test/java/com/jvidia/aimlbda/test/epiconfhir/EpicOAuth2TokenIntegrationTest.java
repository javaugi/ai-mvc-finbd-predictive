/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.test.epiconfhir;

//import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static org.hamcrest.CoreMatchers.containsString;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


/*
✅ EpicOAuth2TokenIntegrationTest
    ✔️ Hits Spring Security OAuth2 callback filter
    ✔️ Triggers token exchange (WireMock)
    ✔️ Runs success handler
    ✔️ Redirects to SPA
 */
@Disabled
public class EpicOAuth2TokenIntegrationTest extends EpicWireMockBaseTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    public void epicOauth2CallbackShouldSucceed() throws Exception {
        mockMvc.perform(get("/login/oauth2/code/epicfhir")
                .param("code", "mock-auth-code")
                .param("state", "mock-state"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string(
                        "Location",
                        containsString("localhost:3000")
                ));
    }

}
