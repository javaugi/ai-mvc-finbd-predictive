/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.security.utils;

public final class PkceParameterNames {

    private PkceParameterNames() {
    }

    /**
     * {@code code_challenge} - used in Authorization Request.
     */
    public static final String CODE_CHALLENGE = "code_challenge";

    /**
     * {@code code_challenge_method} - used in Authorization Request.
     */
    public static final String CODE_CHALLENGE_METHOD = "code_challenge_method";

    /**
     * {@code code_verifier} - used in Token Request.
     */
    public static final String CODE_VERIFIER = "code_verifier";
}
