package io.github.mahjoubech.smartlogiv2.config.oauth2;

import java.util.Map;
public class OktaOAuth2UserInfo implements OAuth2UserInfo {
    private final Map<String , Object> attributes;
    public OktaOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
    @Override
    public String getProviderId() {
        return (String) this.attributes.get("sub");
    }

    @Override
    public String getFirstName() {
        return (String) this.attributes.get("given_name");
    }

    @Override
    public String getLastName() {
        return (String) this.attributes.get("family_name");
    }

    @Override
    public String getEmail() {
        return (String) this.attributes.get("email");
    }

}
