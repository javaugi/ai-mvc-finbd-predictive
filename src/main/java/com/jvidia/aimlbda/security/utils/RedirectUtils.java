/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.security.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RedirectUtils {

    public String determineRedirectUrl(String referer, String origin) {
        // Default to React app
        String redirectUrl = "http://localhost:3000/login";

        if (referer != null) {
            if (referer.contains("localhost:3000")) {
                redirectUrl = "http://localhost:3000/login?logout=true";
            } else if (referer.contains("localhost:5137")) {
                redirectUrl = "http://localhost:4200/login?logout=true";
            } else if (referer.contains("localhost:4200")) {
                redirectUrl = "http://localhost:4200/login?logout=true";
            }
        }

        if (origin != null) {
            if (origin.contains("localhost:3000")) {
                redirectUrl = "http://localhost:3000/login?logout=true";
            } else if (origin.contains("localhost:5137")) {
                redirectUrl = "http://localhost:5137/login?logout=true";
            } else if (origin.contains("localhost:4200")) {
                redirectUrl = "http://localhost:4200/login?logout=true";
            }
        }

        log.debug("determineRedirectUrl referer {}, origin {} \n redirectUrl {}", referer, origin, redirectUrl);
        return redirectUrl;
    }
}
