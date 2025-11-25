/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.utils.types;

import com.jvidia.aimlbda.entity.UserRole;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public enum RoleType {
    ROLE_USER(Set.of("READ_PRIVILEGE", "VIEW_PROFILE")),
    ROLE_ADMIN(Set.of("READ_PRIVILEGE", "VIEW_PROFILE", "WRITE_PRIVILEGE", "DELETE_PRIVILEGE")),
    ROLE_MODERATOR(Set.of("READ_PRIVILEGE", "VIEW_PROFILE", "WRITE_PRIVILEGE")),
    ROLE_AGENT(Set.of("READ_PRIVILEGE", "VIEW_PROFILE", "WRITE_PRIVILEGE", "DELETE_PRIVILEGE")),
    ROLE_VENDOR(Set.of("READ_PRIVILEGE", "VIEW_PROFILE", "WRITE_PRIVILEGE")),
    ROLE_SUPERVISOR(Set.of("READ_PRIVILEGE", "VIEW_PROFILE", "WRITE_PRIVILEGE")),
    ROLE_CASE_WORKER(Set.of("READ_PRIVILEGE", "VIEW_PROFILE", "WRITE_PRIVILEGE")),
    ROLE_PARTICIPANT(Set.of("READ_PRIVILEGE", "VIEW_PROFILE", "WRITE_PRIVILEGE", "DELETE_PRIVILEGE"));


    private final Set<String> privileges;

    RoleType(Set<String> privileges) {
        this.privileges = privileges;
    }

    public Set<String> getPrivileges() {
        return privileges;
    }

    public static Set<String> getPrivilegesByRoleName(String roleName) {
        for (RoleType rt : values()) {
            if (rt.name().equals(roleName)) {
                return rt.getPrivileges();
            }
        }
        return null;
    }

    public static Set<GrantedAuthority> getGrantedAuthoritiesByRoleType(String roleName) {
        Set<GrantedAuthority> authorities = new HashSet<>();

        for (RoleType r : values()) {
            if (r.name().equals(roleName)) {
                authorities = r.getPrivileges().stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toSet());
                authorities.add(new SimpleGrantedAuthority(r.name()));
            }
        }

        return authorities;
    }

    public static Set<GrantedAuthority> getGrantedAuthoritiesByUserRoles(List<UserRole> userRoles) {
        final Set<GrantedAuthority> authorities = new HashSet<>();

        if (userRoles != null && !userRoles.isEmpty()) {
            userRoles.stream().forEach(ur -> {
                authorities.addAll(getGrantedAuthoritiesByRoleType(ur.getRole().getName()));
            });
        }

        return authorities;
    }

}
