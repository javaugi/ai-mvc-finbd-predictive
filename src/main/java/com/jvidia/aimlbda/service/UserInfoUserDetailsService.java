package com.jvidia.aimlbda.service;

import com.jvidia.aimlbda.entity.UserInfo;
import com.jvidia.aimlbda.utils.types.RoleType;
import java.util.Collection;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import org.springframework.security.core.GrantedAuthority;

//This class maps the user info from our db to the user details which is the object stored in spring security.
@lombok.extern.slf4j.Slf4j
@Service
public class UserInfoUserDetailsService implements UserDetailsService {

    private final UserInfoService userInfoService;

    public UserInfoUserDetailsService(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("UserInfoUserDetailsService.loadUserByUsername username {}", username);
        Optional<UserInfo> userInfo = userInfoService.findByEmail(username);
        if (userInfo.isPresent()) {
            UserInfo user = userInfo.get();
            Collection<? extends GrantedAuthority> authorities = RoleType.getGrantedAuthoritiesByUserRoles(user.getUserRoles());
            log.debug("User found: username {} email {} {} with roles: {} authorities size {} ",
                    user.getUsername(), user.getEmail(), user.getUserRoles(), authorities.size());

            return org.springframework.security.core.userdetails.User
                    .withUsername(username)
                    .password(user.getPassword())
                    .authorities(authorities)
                    .accountExpired(false)
                    .accountLocked(false)
                    .credentialsExpired(false)
                    .disabled(!user.isEnabled())
                    .build();
        } else {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        //return userInfo.map(UserInfoUserDetails::new).orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}
