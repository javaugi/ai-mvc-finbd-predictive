/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.clients;

import com.jvidia.aimlbda.entity.Role;
import com.jvidia.aimlbda.entity.UserInfo;
import com.jvidia.aimlbda.entity.UserRole;
import com.jvidia.aimlbda.repository.RoleRepository;
import com.jvidia.aimlbda.repository.UserInfoRepository;
import com.jvidia.aimlbda.repository.UserRoleRepository;
import com.jvidia.aimlbda.utils.types.AuthProvider;
import com.jvidia.aimlbda.utils.types.RoleType;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserInfoClient {

    @Autowired
    RoleRepository roleRepository;
    @Autowired
    UserInfoRepository userInfoRepository;
    @Autowired
    UserRoleRepository userRoleRepository;
    
    public void setup() {
        try {
            setupRoles();
            setupUserInfo();
            setupUserRoles();
            log.debug("UserInfoClient.setup() Role count {}, UserInfo count {} UserRole count {}",
                    roleRepository.count(), userInfoRepository.count(), userRoleRepository.count());
        } catch (Exception ex) {
            log.error("Error setup ", ex);
        }
    }

    private void setupUserRoles() {
        Role roleUser = this.roleRepository.findByRole(RoleType.ROLE_USER.name()).orElse(Role.builder().build());
        Role roleAdmin = this.roleRepository.findByRole(RoleType.ROLE_ADMIN.name()).orElse(Role.builder().build());
        List<UserInfo> users = this.userInfoRepository.findAll();
        if (!users.isEmpty()) {
            List<UserRole> userRoles = new ArrayList<>();
            UserRole userRole;
            for (UserInfo userInfo : users) {
                if (roleUser != null && roleUser.getRole() != null) {
                    Optional<UserRole> optUser = this.userRoleRepository.findByRoleAndUserInfo(roleUser, userInfo);
                    if (!optUser.isPresent()) {
                        userRole = UserRole.builder()
                                .role(roleUser)
                                .userInfo(userInfo)
                                .build();
                        userRoles.add(userRole);
                    }
                }
                if (roleAdmin != null && roleAdmin.getRole() != null) {
                    Optional<UserRole> optAdm = this.userRoleRepository.findByRoleAndUserInfo(roleAdmin, userInfo);
                    if (!optAdm.isPresent()) {
                        userRole = UserRole.builder()
                                .role(roleAdmin)
                                .userInfo(userInfo)
                                .build();
                        userRoles.add(userRole);
                    }
                }
            }

            if (!userRoles.isEmpty()) {
                this.userRoleRepository.saveAllAndFlush(userRoles);
            }
        }

        log.info("Total userRoles {}", userRoleRepository.count());
    }

    private void setupUserInfo() {
        try {
            List<UserInfo> users = new ArrayList<>();
            for (UserInfo entity : USERS) {
                Optional<UserInfo> opt = userInfoRepository.findByEmail(entity.getEmail());
                if (!opt.isPresent()) {
                    users.add(entity);
                }
            }

            if (!users.isEmpty()) {
                userInfoRepository.saveAllAndFlush(users);
            }
            log.info("Total users {}", userInfoRepository.count());
        } catch (BeansException e) {
            log.error("Error setup {}", e);
        }
        
    }

    private void setupRoles() {
        Role entity;
        List<Role> roles = new ArrayList<>();
        Optional<Role> opt;
        for (RoleType rt : RoleType.values()) {
            opt = this.roleRepository.findByRole(rt.name());
            if (!opt.isPresent()) {
                entity = Role.builder().role(rt.name()).build();
                roles.add(entity);
            }
        }

        if (!roles.isEmpty()) {
            this.roleRepository.saveAllAndFlush(roles);
        }
        log.info("Total roles {}", roleRepository.count());
    }

    static final List<UserInfo> USERS = List.of(
            UserInfo.builder().username("javaugi_g")
                    .email("david.lee.remax@gmail.com")
                    .password("admin").name("D Lee").firstName("David")
                    .lastName("Lee").middleInitial("Z")
                    .registrationId(AuthProvider.google.toString())
                    .provider(AuthProvider.google).providerId(AuthProvider.google.toString())
                    .createdDate(OffsetDateTime.now()).updatedDate(OffsetDateTime.now()).build(),
            UserInfo.builder().username("javaugi")
                    .email("javaugi@hotmail.com")
                    .password("admin").name("D Lee").firstName("David")
                    .lastName("Lee").middleInitial("Z")
                    .registrationId(AuthProvider.github.toString())
                    .provider(AuthProvider.github).providerId(AuthProvider.github.toString())
                    .createdDate(OffsetDateTime.now()).updatedDate(OffsetDateTime.now()).build(),
            UserInfo.builder().username("javaugi.loc")
                    .email("javaugi@yahoo.com")
                    .password("admin").name("D Lee").firstName("David")
                    .lastName("Lee").middleInitial("Z")
                    .registrationId(AuthProvider.local.toString())
                    .provider(AuthProvider.local).providerId(AuthProvider.local.toString())
                    .createdDate(OffsetDateTime.now()).updatedDate(OffsetDateTime.now()).build()
    );
}

