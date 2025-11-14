package com.jvidia.aimlbda.service;

import com.jvidia.aimlbda.dto.LoginRequest;
import com.jvidia.aimlbda.dto.SignupRequest;
import com.jvidia.aimlbda.entity.Role;
import com.jvidia.aimlbda.entity.UserInfo;
import com.jvidia.aimlbda.repository.UserInfoRepository;
import com.jvidia.aimlbda.security.exception.DuplicateUserException;
import com.jvidia.aimlbda.security.exception.UserRegistrationException;
import com.jvidia.aimlbda.utils.types.AuthProvider;
import com.jvidia.aimlbda.utils.types.RoleType;
import com.jvidia.aimlbda.utils.mapper.UserInfoMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(
        @Autowired))
public class UserAuthSignupService {

    final UserInfoRepository userInfoRepository;
    final PasswordEncoder passwordEncoder;
    final JwtTokenService jwtService;
    final AuthenticationManager authenticationManager;
    final RoleService roleService;
    final UserInfoMapper mapper;

    /*
    public UserService(AuthenticationManager authenticationManager, JwtTokenService jwtService, PasswordEncoder passwordEncoder, UserInfoRepository userInfoRepository, RoleService roleService, UserInfoMapper mapper) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.userInfoRepository = userInfoRepository;
        this.roleService = roleService;
        this.mapper = mapper;
    }
    // */

    public ResponseEntity<?> authenticate(LoginRequest loginRequest) {
        try {
            log.debug("authenticate loginRequest {} auth.isAuthenticated() {}", loginRequest);
            Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
            log.debug("authenticate auth.isAuthenticated() {}", auth.isAuthenticated());

            if (auth.isAuthenticated()) {
                Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
                String jwtToken = jwtService.generateToken(loginRequest.getEmail(), authorities);
                return ResponseEntity.ok().body(jwtToken);
            } else {
                throw new BadCredentialsException("Invalid username or password");
            }
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    public ResponseEntity<?> register(SignupRequest signupRequest) {
        try{
            UserInfo user = mapper.singupRequestDtoToUserInfo(signupRequest);
            log.debug("register signupRequest {} user {}", signupRequest, user);
            user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
            user.setEmail(signupRequest.getEmail());
            user.setName(signupRequest.getName());
            user.setProvider(AuthProvider.local);
            user.setProviderId("LOCAL");
            user.setEmailVerified(false);
            user.setImageUrl(null);
            List<Role> roles = new ArrayList<>();
            roles.add(roleService.getRoleByName(RoleType.ROLE_USER));
            roleService.giveRolesToUser(user, roles);
            userInfoRepository.save(user);
            return ResponseEntity.ok().body("Registration Successful");

        }catch (DataIntegrityViolationException e){
            throw new DuplicateUserException("Invalid Registration Request");
        }
        catch (Exception e){
            throw new UserRegistrationException("Invalid Registration Request");
        }
    }

}
