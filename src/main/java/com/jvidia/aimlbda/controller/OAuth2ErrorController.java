/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.controller;

import com.jvidia.aimlbda.security.utils.RedirectUtils;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.web.WebAttributes;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@lombok.RequiredArgsConstructor
@RestController
public class OAuth2ErrorController implements ErrorController {

    private final RedirectUtils redirectUtils;

    @GetMapping("/login")
    public void loginRedirect(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String referer = request.getHeader("Referer");
        String origin = request.getHeader("Origin");
        String redirectUrl = redirectUtils.determineRedirectUrl(referer, origin);
        log.info("OAuth2ErrorController loginRedirect referer {}, origin {} \n redirectUrl {}", referer, origin, redirectUrl);
        log.info("OAuth2ErrorController stops login from propagating further in the loop!");
    }

    @GetMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object exception = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);

        log.error("OAuth2 Error - Status: {}, Exception: {}, Message: {}",
                status, exception, message);

        if (exception != null) {
            log.error("Full exception stack trace:", (Throwable) exception);
        }

        // Check for OAuth2 specific attributes
        AuthenticationException authEx
                = (AuthenticationException) request.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);

        if (authEx != null) {
            log.error("Authentication Exception: {}", authEx.getMessage(), authEx);
            model.addAttribute("error", authEx.getMessage());

            if (authEx instanceof OAuth2AuthenticationException) {
                OAuth2Error error = ((OAuth2AuthenticationException) authEx).getError();
                log.error("OAuth2 Error: {}, Description: {}, URI: {}",
                        error.getErrorCode(), error.getDescription(), error.getUri());
                model.addAttribute("oauthError", error);
            }
        }

        return "error";
    }


}
