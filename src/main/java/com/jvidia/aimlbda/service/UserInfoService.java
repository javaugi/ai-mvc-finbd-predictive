/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.service;

import com.jvidia.aimlbda.entity.UserInfo;
import com.jvidia.aimlbda.repository.UserInfoRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@lombok.extern.slf4j.Slf4j
@Service
@RequiredArgsConstructor
public class UserInfoService {

    private final UserInfoRepository userInfoRepository;

    public List<UserInfo> findAll() {
        return this.userInfoRepository.findAll();
    }

    public Optional<UserInfo> findById(Long id) {
        return this.userInfoRepository.findById(id);
    }

    public Optional<UserInfo> findByEmail(String email) {
        return this.userInfoRepository.findByEmail(email);
    }

    public Optional<UserInfo> findByUsername(String username) {
        return this.userInfoRepository.findByUsername(username);
    }

    public Optional<UserInfo> findByEmailOrUsername(String email, String username) {
        Optional<UserInfo> opt = Optional.empty();
        log.debug("1. findByEmailOrUsername email {} username {}", email, username);
        if (StringUtils.isNotEmpty(email)) {
            opt = this.userInfoRepository.findByEmail(email);
        } else if (StringUtils.isNotEmpty(username)) {
            opt = this.userInfoRepository.findByUsername(username);
        }
        log.debug("2. findByEmailOrUsername opt UserInfo {} ", (opt.isPresent() ? opt.get() : null));
        return opt;
    }

    public UserInfo save(UserInfo userInfo) {
        return this.userInfoRepository.save(userInfo);
    }
}
