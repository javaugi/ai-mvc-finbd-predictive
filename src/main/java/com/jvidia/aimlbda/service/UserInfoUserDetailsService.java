package com.jvidia.aimlbda.service;

import com.jvidia.aimlbda.dto.UserInfoUserDetails;
import com.jvidia.aimlbda.entity.UserInfo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
        return userInfo.map(UserInfoUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}