package com.jvidia.aimlbda.controller;

import com.jvidia.aimlbda.entity.UserInfo;
import com.jvidia.aimlbda.security.exception.UserNotFoundException;
import com.jvidia.aimlbda.service.UserInfoService;
import java.util.Collection;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserInfoController {
    final UserInfoService userInfoService;

    @GetMapping
    public ResponseEntity<Collection<UserInfo>> getAllUsers() {
        return ResponseEntity.ok(userInfoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserInfo> getById(Long id) {
        Optional<UserInfo> opt = userInfoService.findById(id);
        if (opt.isPresent()) {
            return ResponseEntity.ok(opt.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{email}")
    public ResponseEntity<UserInfo> getByEmail(String email) {
        Optional<UserInfo> opt = userInfoService.findByEmail(email);
        if (opt.isPresent()) {
            return ResponseEntity.ok(opt.get());
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/exception")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> testUserException() {
        throw new UserNotFoundException("User");
    }

}
