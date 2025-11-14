package com.jvidia.aimlbda.service;

import com.jvidia.aimlbda.entity.Role;
import com.jvidia.aimlbda.entity.UserInfo;
import com.jvidia.aimlbda.security.oauth2.user.OAuth2UserFactory;
import com.jvidia.aimlbda.security.oauth2.user.OAuth2UserInfo;
import com.jvidia.aimlbda.utils.LogUtils;
import com.jvidia.aimlbda.utils.types.AuthProvider;
import com.jvidia.aimlbda.utils.types.RoleType;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.web.client.RestTemplate;

@lombok.extern.slf4j.Slf4j
@Service
public class OAuthUserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserInfoService userInfoService;
    private final RoleService roleService;

    public OAuthUserService(UserInfoService userInfoService, RoleService roleService) {
        this.userInfoService = userInfoService;
        this.roleService = roleService;
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.debug("OAuthUserService.loaduser OAuth2UserRequest {}", userRequest);
        OAuth2UserService<OAuth2UserRequest, OAuth2User> defaultOauthUserService = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = defaultOauthUserService.loadUser(userRequest);
        log.debug("OAuthUserService.loaduser oAuth2User name {} authorities {} ",
                oAuth2User.getName(), oAuth2User.getAuthorities());
        LogUtils.logMap("OAuthUserService.loadUser", oAuth2User.getAttributes());

        String provider = userRequest.getClientRegistration().getRegistrationId();
        // Only override GitHub behavior
        if (provider.equals("github")) {
            oAuth2User = getUserInfoForGithib(userRequest, oAuth2User);
        }

        try {
            return processOAuth2User(userRequest, oAuth2User);
        } catch (Exception ex) {
            log.error("Error loadUser userRequest {}", userRequest, ex);
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex);
        }
    }

    private OAuth2User getUserInfoForGithib(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        // Access token
        String token = userRequest.getAccessToken().getTokenValue();
        // Fetch GitHub email list
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "token " + token);

        HttpEntity<String> entity = new HttpEntity<>("", headers);
        ResponseEntity<List<Map<String, Object>>> response
                = restTemplate.exchange(
                        "https://api.github.com/user/emails",
                        HttpMethod.GET,
                        entity,
                        new ParameterizedTypeReference<>() {
                }
                );

        String email = retrieveEmail(response);

        // Add email into attributes
        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
        attributes.put("email", email);
        return new DefaultOAuth2User(
                oAuth2User.getAuthorities(),
                attributes,
                "id" // GitHub unique identifier
        );
    }

    private String retrieveEmail(ResponseEntity<List<Map<String, Object>>> response) {
        if (response == null || response.getBody() == null) {
            return null;
        }
        List<Map<String, Object>> data = response.getBody();
        if (data != null && !data.isEmpty()) {
            for (Map<String, Object> emailData : data) {
                Boolean primary = (Boolean) emailData.get("primary");
                if (primary) {
                    return (String) emailData.get("email");
                }
            }
        }

        return null;
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        log.debug("processOAuth2User \n *** oAuth2UserRequest {}, \n *** oAuth2User {}", oAuth2UserRequest, oAuth2User);

        OAuth2UserInfo oAuth2UserInfo = OAuth2UserFactory.getOAuth2User(
                oAuth2UserRequest.getClientRegistration().getRegistrationId(), oAuth2User.getAttributes());

        String registrationId = oAuth2UserInfo.getRegistrationId();
        log.debug("processOAuth2User orign registrationId {} attr registrationId {} \n oAuth2UserInfo {}",
                oAuth2UserRequest.getClientRegistration().getRegistrationId(), registrationId, oAuth2UserInfo);

        Optional<UserInfo> optionalUser = Optional.empty();
        switch (registrationId) {
            case "google" -> {
                if (oAuth2UserInfo.getEmail().isEmpty()) {
                    throw new OAuth2AuthenticationException("Email not found for the oauth user");
                }
                optionalUser = userInfoService.findByEmail(oAuth2UserInfo.getEmail());
            }
            case "github", "local" -> {
                log.debug("processOAuth2User switch by registrationId (github/local?)= {} ", registrationId);
                if (oAuth2UserInfo.getUsername().isEmpty()) {
                    throw new OAuth2AuthenticationException("Username not found for the oauth user");
                }
                optionalUser = userInfoService.findByUsername(oAuth2UserInfo.getUsername());
            }
            default ->
                throw new OAuth2AuthenticationException("Email or Username not found for the oauth user");
        }

        UserInfo user;
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
            if (!user.getProvider().equals(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()))) {
                throw new OAuth2AuthenticationException("Looks like you're signed up with "
                        + user.getProvider() + " account. Please use your " + user.getProvider()
                        + " account to login.");
            }
            updateExistingUser(user, oAuth2UserInfo);
        } else {
            registerNewUser(oAuth2UserRequest, oAuth2UserInfo);
        }

        return oAuth2User;
    }

    private void registerNewUser(OAuth2UserRequest oAuth2UserRequest, OAuth2UserInfo oAuth2UserInfo) {
        log.debug("OAuthUserService.registerNewUser oAuth2UserInfo {}", oAuth2UserInfo);
        UserInfo user = new UserInfo();
        user.setProvider(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()));
        user.setProviderId(oAuth2UserInfo.getId());
        user.setRegistrationId(oAuth2UserInfo.getRegistrationId());
        user.setUsername(oAuth2UserInfo.getUsername());
        updateUserDetails(user, oAuth2UserInfo);

        setRoles(user);

        userInfoService.save(user);
    }

    private void updateExistingUser(UserInfo existingUser, OAuth2UserInfo oAuth2UserInfo) {
        log.debug("OAuthUserService.updateExistingUser \n existingUser {} \n oAuth2UserInfo {}", existingUser, oAuth2UserInfo);
        if (existingUser.getUserRoles() == null || existingUser.getUserRoles().isEmpty()) {
            setRoles(existingUser);
        }
        updateUserDetails(existingUser, oAuth2UserInfo);
        userInfoService.save(existingUser);
    }

    private void updateUserDetails(UserInfo user, OAuth2UserInfo oAuth2UserInfo) {
        user.setName(oAuth2UserInfo.getName());
        user.setEmail(oAuth2UserInfo.getEmail());
        user.setImageUrl(oAuth2UserInfo.getImageUrl());
        user.setRegistrationId(oAuth2UserInfo.getRegistrationId());
        user.setUsername(oAuth2UserInfo.getUsername());
    }

    private void setRoles(UserInfo user) {
        List<Role> userRoles = new ArrayList<>();
        userRoles.add(roleService.getRoleByName(RoleType.ROLE_USER));
        roleService.giveRolesToUser(user, userRoles);
    }

}
