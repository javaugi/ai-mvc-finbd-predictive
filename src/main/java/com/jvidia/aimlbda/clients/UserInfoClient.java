/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.clients;

import com.jvidia.aimlbda.entity.Privilege;
import com.jvidia.aimlbda.entity.Role;
import com.jvidia.aimlbda.entity.UserInfo;
import com.jvidia.aimlbda.entity.UserRole;
import com.jvidia.aimlbda.repository.PrivilegeRepository;
import com.jvidia.aimlbda.repository.RoleRepository;
import com.jvidia.aimlbda.repository.UserInfoRepository;
import com.jvidia.aimlbda.repository.UserRoleRepository;
import com.jvidia.aimlbda.utils.types.AuthProvider;
import com.jvidia.aimlbda.utils.types.RoleType;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserInfoClient {

    final RoleRepository roleRepository;
    final UserInfoRepository userInfoRepository;
    final UserRoleRepository userRoleRepository;
    final PrivilegeRepository privilegeRepository;
    
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
        Role roleUser = this.roleRepository.findByName(RoleType.ROLE_USER.name()).orElse(Role.builder().build());
        Role roleAdmin = this.roleRepository.findByName(RoleType.ROLE_ADMIN.name()).orElse(Role.builder().build());
        List<UserInfo> users = this.userInfoRepository.findAll();
        if (!users.isEmpty()) {
            List<UserRole> userRoles = new ArrayList<>();
            UserRole userRole;
            for (UserInfo userInfo : users) {
                if (roleUser != null && roleUser.getName() != null) {
                    Optional<UserRole> optUser = this.userRoleRepository.findByRoleAndUserInfo(roleUser, userInfo);
                    if (!optUser.isPresent()) {
                        userRole = UserRole.builder()
                                .role(roleUser)
                                .userInfo(userInfo)
                                .build();
                        userRoles.add(userRole);
                    }
                }
                if (roleAdmin != null && roleAdmin.getName() != null) {
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
                this.userRoleRepository.saveAll(userRoles);
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
                userInfoRepository.saveAll(users);
            }
            log.info("Total users {}", userInfoRepository.count());
        } catch (BeansException e) {
            log.error("Error setup {}", e);
        }
        
    }

    private void setupRoles() {
        Role entity;
        Privilege privEntity;
        List<Role> roles = new ArrayList<>();
        Optional<Role> opt;
        for (RoleType rt : RoleType.values()) {
            opt = this.roleRepository.findByName(rt.name());
            if (!opt.isPresent()) {
                Set<Privilege> prevEntities = new HashSet<>();
                Set<String> privs = RoleType.getPrivilegesByRoleName(rt.name());
                for (String str : privs) {
                    Optional<Privilege> optPriv = this.privilegeRepository.findByName(str);
                    if (optPriv.isPresent()) {
                        prevEntities.add(optPriv.get());
                    } else {
                        privEntity = Privilege.builder().name(str).build();
                        this.privilegeRepository.save(privEntity);
                        prevEntities.add(privEntity);
                    }
                }

                entity = Role.builder()
                        .name(rt.name()).privileges(prevEntities)
                        .build();
                roles.add(entity);
            }
        }

        if (!roles.isEmpty()) {
            this.roleRepository.saveAll(roles);
        }
        log.info("Total roles {}", roleRepository.count());
    }

    //$2a$10$otFk6PYeXqFIPHp9rrN9gOvelESYT.a9dUa.evkcnbVEnhu3zeyry=MyAdminPwd
    static final List<UserInfo> USERS = List.of(
            UserInfo.builder().username("javaugi_g")
                    .email("david.lee.remax@gmail.com")
                    .password("$2a$10$otFk6PYeXqFIPHp9rrN9gOvelESYT.a9dUa.evkcnbVEnhu3zeyry").name("D Lee").firstName("David")
                    .lastName("Lee").middleInitial("Z")
                    .registrationId(AuthProvider.google.toString())
                    .provider(AuthProvider.google).providerId(AuthProvider.google.toString())
                    .createdDate(OffsetDateTime.now()).updatedDate(OffsetDateTime.now()).build(),
            UserInfo.builder().username("javaugi")
                    .email("javaugi@hotmail.com")
                    .password("$2a$10$otFk6PYeXqFIPHp9rrN9gOvelESYT.a9dUa.evkcnbVEnhu3zeyry").name("D Lee").firstName("David")
                    .lastName("Lee").middleInitial("Z")
                    .registrationId(AuthProvider.github.toString())
                    .provider(AuthProvider.github).providerId(AuthProvider.github.toString())
                    .createdDate(OffsetDateTime.now()).updatedDate(OffsetDateTime.now()).build(),
            UserInfo.builder().username("javaugi.loc")
                    .email("dlee9591@gmail.com")
                    .password("$2a$10$otFk6PYeXqFIPHp9rrN9gOvelESYT.a9dUa.evkcnbVEnhu3zeyry").name("D Lee").firstName("David")
                    .lastName("Lee").middleInitial("Z")
                    .registrationId(AuthProvider.local.toString())
                    .provider(AuthProvider.local).providerId(AuthProvider.local.toString())
                    .createdDate(OffsetDateTime.now()).updatedDate(OffsetDateTime.now()).build()
    );
}

