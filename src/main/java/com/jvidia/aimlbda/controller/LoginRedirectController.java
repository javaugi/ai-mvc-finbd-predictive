/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.controller;

import com.jvidia.aimlbda.security.handler.LoginLogoutRedirectHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@lombok.RequiredArgsConstructor
@RestController
public class LoginRedirectController {
    private final LoginLogoutRedirectHandler redirectHandler;

    @GetMapping("/login")
    public void loginRedirect(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String referer = request.getHeader("Referer");
        String origin = request.getHeader("Origin");
        String redirectUrl = redirectHandler.determineRedirectUrl(referer, origin);
        log.info("loginRedirect referer {}, origin {} \n redirectUrl {}", referer, origin, redirectUrl);
        log.info("LoginRedirectController stops login from propagating further in the loop!");
    }

}
