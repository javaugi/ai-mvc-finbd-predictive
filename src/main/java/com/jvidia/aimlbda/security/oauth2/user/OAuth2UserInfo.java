package com.jvidia.aimlbda.security.oauth2.user;

import java.util.Map;

public interface OAuth2UserInfo {

    public Map<String, Object> getAttributes();

    public String getRegistrationId();

    public String getId();

    public String getName();

    public String getEmail();

    public String getUsername();

    public String getImageUrl();

}
