/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.security.utils;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.stereotype.Component;

/*
7) Using encrypted DB passwords with Jasypt
    If you want to store encrypted DB passwords in application-*.properties:
    Generate encrypted value (quick Java helper):
        Create a small class to encrypt using same algorithm as jasypt defaults (PBEWithMD5AndDES):
 */
@Component
public class JasyptEncryptorUtil {
    public static void main(String[] args) {
        StandardPBEStringEncryptor enc = new StandardPBEStringEncryptor();
        enc.setPassword("mysupersecretpassword"); // your JASYPT_ENCRYPTOR_PASSWORD
        //-Djasypt.encryptor.password=mysupersecretpassword
        // defaults align with jasypt-spring-boot-starter
        String cipherDev = enc.encrypt("devpass");
        System.out.println("DEV=" + "ENC(" + cipherDev + ")");
        String cipherTest = enc.encrypt("testpass");
        System.out.println("TEST=" + "ENC(" + cipherTest + ")");
        String cipherProd = enc.encrypt("prodpass");
        System.out.println("PROD=" + "ENC(" + cipherProd + ")");
    }
}
