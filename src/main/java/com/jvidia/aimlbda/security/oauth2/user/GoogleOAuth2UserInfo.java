package com.jvidia.aimlbda.security.oauth2.user;

import java.util.Map;

public class GoogleOAuth2UserInfo implements OAuth2UserInfo {

    protected Map<String, Object> attributes;

    public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getRegistrationId() {
        return (String) attributes.get("registrationId");
    }

    @Override
    public String getId() {
        return (String) attributes.get("sub");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getUsername() {
        return (String) attributes.get("login");
    }

    @Override
    public String getImageUrl() {
        return (String) attributes.get("picture");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("\n START GoogleOAuth2UserInfo attributes");
        for (String key : attributes.keySet()) {
            sb.append("## attributes key=").append(key).append(", value=").append(attributes.get(key));
        }
        sb.append("END GoogleOAuth2UserInfo attributes");

        return sb.toString();
    }
}
