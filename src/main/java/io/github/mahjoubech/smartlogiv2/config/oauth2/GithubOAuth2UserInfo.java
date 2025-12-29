package io.github.mahjoubech.smartlogiv2.config.oauth2;

import java.util.Map;

public class GithubOAuth2UserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;
    public GithubOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
    @Override
    public String getProviderId() {
        Object id = attributes.get("id");
        return id != null ? id.toString() : null;
    }

    @Override
    public String getFirstName() {
        return (String)  attributes.get("given_name");
    }

    @Override
    public String getLastName() {
        return (String)   attributes.get("family_name");
    }

    @Override
    public String getEmail() {
        return (String)  attributes.get("email");
    }
}
