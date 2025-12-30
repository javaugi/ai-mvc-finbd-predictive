/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.MapPropertySource;

@Slf4j
public class LogUtil {

    public static void logBeans(String[] beans) {
        log.debug("\n## MyApplication START LOGGING {} ");
        Arrays.sort(beans);
        for (String bean : beans) {
            log.debug(bean);
        }
        log.debug("## End LOGGING");
    }

    public static void logMap(String from, Map<String, Object> map) {
        log.debug("\n## START LOGGING {} ", from);
        for (String key : map.keySet()) {
            log.debug(" key {} value {}", key, map.get(key));
        }
        log.debug("## End LOGGING {} ", from);
    }

    public static void logRequest(String from, HttpServletRequest req) {
        log.debug("\n## START LOGGING {} ", from);
        log.debug("authType {}, contentType {}, contextPath {}", req.getAuthType(), req.getContentType(), req.getContextPath());
        log.debug("Local Addr {}, name {}, port {}", req.getLocalAddr(), req.getLocalName(), req.getLocalPort());
        log.debug("Remote Addr {}, name {}, port {}, \n  ## user {} userPrincipal {}",
                req.getRemoteAddr(), req.getRemoteHost(), req.getRemotePort(), req.getRemoteUser(), req.getUserPrincipal());
        log.debug("Server Name {}, port {}, path {}, name {}", req.getServerName(), req.getServerPort(), req.getServletPath(), req.getServerName());
        log.debug("method {}, pathInfo {}, requestURI {} \n queryString {}", req.getMethod(), req.getPathInfo(), req.getRequestURI(), req.getQueryString());

        if (req.getCookies() != null) {
            for (Cookie cookie : req.getCookies()) {
                log.debug("Cookie name {}, value {}, path {} \n queryString {}", cookie.getName(), cookie.getValue(), cookie.getPath());
            }
        }

        req.getAttributeNames().asIterator()
                .forEachRemaining(k -> {
                    log.debug("Attr key {}, value {}", k, req.getAttribute(k));
                });
        req.getHeaderNames().asIterator()
                .forEachRemaining(k -> {
                    log.debug("HEADER key {}, value {}", k, req.getAttribute(k));
                });
        req.getParameterNames().asIterator()
                .forEachRemaining(k -> {
                    log.debug("PARAM key {}, value {}", k, req.getAttribute(k));
                });
        log.debug("## End LOGGING {} ", from);
    }

    public static void logRunnerMap(String from, MapPropertySource mapPropertySource) {
        log.debug("\n## START LOGGING {} ", from);
        mapPropertySource.getSource().keySet()
                .stream()
                .filter(k -> (k.startsWith("spring.datasource")))
                .forEach(k -> {
                    log.debug("ENV key={}  value={}", k, mapPropertySource.getSource().get(k));
                }
                );
        log.debug("## End LOGGING {} ", from);
    }
}
