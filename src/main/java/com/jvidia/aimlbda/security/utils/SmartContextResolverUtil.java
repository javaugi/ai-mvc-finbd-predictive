/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.security.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class SmartContextResolverUtil {

    public Map<String, String> resolveSmartContext(String idToken) {
        // Decode JWT to get SMART launch context
        Map<String, String> context = new HashMap<>();

        try {
            String[] parts = idToken.split("\\.");
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
            JsonNode jsonNode = new ObjectMapper().readTree(payload);

            // Extract SMART context parameters
            if (jsonNode.has("patient")) {
                context.put("patient", jsonNode.get("patient").asText());
            }
            if (jsonNode.has("encounter")) {
                context.put("encounter", jsonNode.get("encounter").asText());
            }
            if (jsonNode.has("need_patient_banner")) {
                context.put("needPatientBanner", jsonNode.get("need_patient_banner").asText());
            }

        } catch (JsonProcessingException e) {
            // Handle decoding error
        }

        return context;
    }
}
