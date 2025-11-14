/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.security.converter;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
//import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties.Jwt;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 *
 * @author javaugi
 */
public class KeycloakRealmRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    
    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Map<String, Object> realmAccess = (Map<String, Object>) jwt.getClaims().get("realm_access");
        
        if (realmAccess == null || realmAccess.isEmpty() || realmAccess.get("roles") == null) {
            return List.of();
        }
        
        Collection<GrantedAuthority> returnValue = ((List<String>) realmAccess.get("roles"))
            .stream()
            .map(roleName -> "ROLE_" + roleName.toUpperCase())
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
            
        return returnValue;
    }
    
}
