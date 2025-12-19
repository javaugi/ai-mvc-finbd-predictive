/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.security.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import org.springframework.stereotype.Component;

@Component
public class PkceUtil {

    private static final String CODE_VERIFIER_CHARS
            = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-._~";
    private static final int CODE_VERIFIER_LENGTH = 128;

    public String generateCodeVerifier() {
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder sb = new StringBuilder(CODE_VERIFIER_LENGTH);

        for (int i = 0; i < CODE_VERIFIER_LENGTH; i++) {
            int randomIndex = secureRandom.nextInt(CODE_VERIFIER_CHARS.length());
            sb.append(CODE_VERIFIER_CHARS.charAt(randomIndex));
        }

        return sb.toString();
    }

    public String generateCodeChallenge(String codeVerifier) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] digest = md.digest(codeVerifier.getBytes(StandardCharsets.US_ASCII));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
    }

    public String generateCodeChallengeWithMethod(String codeVerifier, String method)
            throws NoSuchAlgorithmException {
        if (null == method) {
            throw new IllegalArgumentException("Unsupported code challenge method: " + method);
        } else
            switch (method) {
                case "S256" -> {
                    return generateCodeChallenge(codeVerifier);
                }
                case "plain" -> {
                    return codeVerifier;
                }
                default ->
                    throw new IllegalArgumentException("Unsupported code challenge method: " + method);
            }
    }
}
