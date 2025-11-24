package com.jvidia.aimlbda.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.SerializationUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

public class CookieUtils {
    // Inject the ObjectMapper bean (e.g., via constructor injection in a service/component)
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Optional<Cookie> getCookie(HttpServletRequest request, String name){
        Cookie[] cookies = request.getCookies();

        if(cookies != null){
            for(Cookie cookie: cookies) {
                if(cookie.getName().equals(name)){
                    return Optional.of(cookie);
                }
            }
        }

        return Optional.empty();
    }

    public static void setCookie(HttpServletResponse response, String name, String value, int maxAge){
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAge);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name){
        Cookie[] cookies = request.getCookies();
        if(cookies != null){
            for(Cookie cookie: cookies) {
                if(cookie.getName().equals(name)){
                    cookie.setMaxAge(0);
                    cookie.setPath("/");
                    cookie.setValue("");
                    response.addCookie(cookie);
                }
            }
        }
    }

    public static String searlizeCookie(Object object){
        return Base64.getUrlEncoder()
                .encodeToString(SerializationUtils.serialize(object));
    }

    @SuppressWarnings("deprecation")
    public static <T> T deserialize(Cookie cookie, Class<T> clas){
        return clas.cast(
                SerializationUtils.deserialize(Base64.getUrlDecoder().decode(cookie.getValue()))
        );
    }

    // You would also need a corresponding serialize method
    public static String searlizeCookieNew(Object object) {
        try {
            // 1. Serialize the object to a JSON string
            String jsonString = objectMapper.writeValueAsString(object);
            
            // 2. Encode the JSON string bytes to Base64
            String base64Encoded = Base64.getUrlEncoder().encodeToString(jsonString.getBytes(StandardCharsets.UTF_8));
            
            // 3. URL-encode the Base64 value for safe storage in a cookie
            return URLEncoder.encode(base64Encoded, StandardCharsets.UTF_8.name());
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize object for cookie", e);
        }
    }    

    // The new deserialize method using Jackson
    public static <T> T deserializeNew(Cookie cookie, Class<T> clas) {
        try {
            // 1. Decode the URL-encoded cookie value
            String decodedCookieValue = URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8.name());
            
            // 2. Decode the Base64 value
            byte[] decodedBytes = Base64.getUrlDecoder().decode(decodedCookieValue);
            
            // 3. Convert bytes to a String (assuming the bytes represent a JSON string)
            String jsonString = new String(decodedBytes, StandardCharsets.UTF_8);

            // 4. Deserialize the JSON string using Jackson
            return objectMapper.readValue(jsonString, clas);
        } catch (IOException e) {
            // Handle exceptions appropriately (e.g., log the error, throw a specific exception)
            throw new RuntimeException("Failed to deserialize cookie value", e);
        }
    }

}
