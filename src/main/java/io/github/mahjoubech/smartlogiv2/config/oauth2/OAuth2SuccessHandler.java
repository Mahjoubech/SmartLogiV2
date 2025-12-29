package io.github.mahjoubech.smartlogiv2.config.oauth2;

import io.github.mahjoubech.smartlogiv2.config.JwtService;
import io.github.mahjoubech.smartlogiv2.exception.ResourceNotFoundException;
import io.github.mahjoubech.smartlogiv2.model.entity.RolesEntity;
import io.github.mahjoubech.smartlogiv2.model.entity.User;
import io.github.mahjoubech.smartlogiv2.model.enums.AuthProvider;
import io.github.mahjoubech.smartlogiv2.model.enums.Roles;
import io.github.mahjoubech.smartlogiv2.repository.RolesEntityRepository;
import io.github.mahjoubech.smartlogiv2.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;


@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
    private final UserRepository userRepository;
    private final JwtService  jwtService;
    private final RolesEntityRepository  rolesEntityRepository;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken  token = (OAuth2AuthenticationToken) authentication;
        String provider = token.getAuthorizedClientRegistrationId();
        OAuth2User oAuthUser = token.getPrincipal();
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(provider, oAuthUser.getAttributes());
        String username = (String) oAuthUser.getAttributes().get("name");
        final String fistname ;
        final String lastname ;
        switch(provider.toLowerCase()) {
            case "google":
                       fistname = userInfo.getFirstName();
                       lastname = userInfo.getLastName();
                       break;
            case "facebook":
                       String[] parts = username.split(" ");
                       fistname = parts[0];
                       lastname = parts[parts.length - 1];
                       break;
            case "github":
                       String[] prt = username.split(" ");
                       fistname = prt[0];
                       lastname = prt[prt.length - 1];
                       break;
            case "okta":
                    String sub = (String)oAuthUser.getAttributes().get("sub");
                    if(sub.contains("google")){
                        fistname = userInfo.getFirstName();
                        lastname = userInfo.getLastName();
                        break;
                    }else if(sub.contains("facebook")){
                        String[] prts = username.split(" ");
                        fistname = prts[0];
                        lastname = prts[prts.length - 1];
                        break;
                    } else if (sub.contains("github")) {
                        String[] prt2 = username.split(" ");
                        fistname = prt2[0];
                        lastname = prt2[prt2.length - 1];
                        break;
                    }

            default:
                fistname = username;
                lastname = username;

        }
        User user = userRepository.findByEmail(userInfo.getEmail())
                .orElseGet(()->{
                    User u =  new User();
                    u.setEmail(userInfo.getEmail());
                    u.setNom(lastname);
                    u.setPrenom(fistname);
                    u.setTelephone(null);
                    u.setPassword("azerty");
                    RolesEntity rolesEntity = rolesEntityRepository.findByName(Roles.CLIENT)
                                    .orElseThrow(()-> new ResourceNotFoundException("role Client not fond"));
                    u.setRole(rolesEntity);
                    return  u;
                });
        user.setProvider(AuthProvider.valueOf(provider.toUpperCase()));
        user.setProviderId(userInfo.getProviderId());
        userRepository.save(user);
        var jwt = jwtService.generateToken(user);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().println(jwt);
    }
}
