package io.github.mahjoubech.smartlogiv2.config.oauth2;

public interface OAuth2UserInfo {
    String getProviderId();
    String getFirstName();
    String getLastName();
    String getEmail();
}
