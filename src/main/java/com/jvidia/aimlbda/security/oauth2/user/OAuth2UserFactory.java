package com.jvidia.aimlbda.security.oauth2.user;

import com.jvidia.aimlbda.utils.LogUtil;
import com.jvidia.aimlbda.utils.types.AuthProvider;
import java.util.HashMap;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OAuth2UserFactory {

    public static OAuth2UserInfo getOAuth2User(String registrationId, Map<String, Object> attributes) {
        Map<String, Object> userAttributes = getDeepCopy(attributes);
        userAttributes.put("registrationId", registrationId);
        if (registrationId.equalsIgnoreCase(AuthProvider.github.toString())
                || registrationId.equalsIgnoreCase(AuthProvider.local.toString())) {
            userAttributes.put("username", userAttributes.get("login"));
        }
        LogUtil.logMap("OAuth2UserFactory.getOAuth2User", userAttributes);

        if (registrationId.equalsIgnoreCase(AuthProvider.google.toString())) {
            return new GoogleOAuth2UserInfo(userAttributes);
        } else if (registrationId.equalsIgnoreCase((AuthProvider.github.toString()))) {
            return new GithubOAuth2UserInfo(userAttributes);
        } else if (registrationId.equalsIgnoreCase((AuthProvider.epicfhir.toString()))) {
            return new EpicFhirOAuth2UserInfo(userAttributes);
        } else if (registrationId.equalsIgnoreCase((AuthProvider.local.toString()))) {
            return new InternalOAuth2UserInfo(userAttributes);
        } else {
            throw new OAuth2AuthenticationException("Sorry! Login with " + registrationId + " is not supported yet.");
        }
    }

    private static Map<String, Object> getDeepCopy(Map<String, Object> attributes) {
        Map<String, Object> userAttributes = new HashMap<>();
        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            userAttributes.put(entry.getKey(), entry.getValue()); // Deep copy of MyValue
        }

        return userAttributes;
    }
}
