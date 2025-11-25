package com.jvidia.aimlbda.service;

import com.jvidia.aimlbda.entity.Role;
import com.jvidia.aimlbda.entity.UserInfo;
import com.jvidia.aimlbda.entity.UserRole;
import com.jvidia.aimlbda.repository.RoleRepository;
import com.jvidia.aimlbda.utils.types.RoleType;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role getRoleByName(RoleType roleName) {
        return roleRepository.findByName(roleName.name())
                .orElse(Role.builder().build());
    }

    public void giveRolesToUser(UserInfo user, List<Role> roles) {
        List<UserRole> userRoles = roles.stream()
                .map(role -> UserRole.builder()
                        .role(role)
                        .userInfo(user)
                        .build())
                .collect(Collectors.toList());

        user.setUserRoles(userRoles);
    }

}
