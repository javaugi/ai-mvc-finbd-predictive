/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

/**
 *
 * @author javau
 */
public interface JwtUtils {
    Boolean validateToken(String token);

    Boolean validateToken(String token, UserDetails userDetails);

    Authentication getAuthentication(String token);

    String generateToken(UserDetails userDetails);

    String generateToken(Authentication auth);
    String extractUsername(String token);
}
