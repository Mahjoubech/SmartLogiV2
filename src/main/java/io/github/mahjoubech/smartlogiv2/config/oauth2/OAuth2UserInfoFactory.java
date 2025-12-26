package io.github.mahjoubech.smartlogiv2.config.oauth2;

import java.util.Map;

public class OAuth2UserInfoFactory {
    public static OAuth2UserInfo getOAuth2UserInfo( String registrationId , Map<String, Object> attributes) {
      return switch(registrationId.toLowerCase()){
          case "google" -> new GoogleOAuth2UserInfo(attributes);
          case "facebook" -> new FacebookOAuth2UserInfo(attributes);
          default -> throw new IllegalArgumentException("Unsupported provider");
      };
    }
}
