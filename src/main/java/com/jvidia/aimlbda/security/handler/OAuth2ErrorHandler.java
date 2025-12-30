/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.security.handler;

import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

@Component
public class OAuth2ErrorHandler {

    @EventListener
    public void handleAuthenticationFailure(AuthenticationFailureBadCredentialsEvent event) {
        System.out.println("Authentication failed: " + event.getException().getMessage());
        System.out.println("Cause: " + event.getException().getCause());
    }
}
