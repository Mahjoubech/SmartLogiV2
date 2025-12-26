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
        OAuth2User oAuthUser = token.getPrincipal();
        String email =  oAuthUser.getAttribute("email");
        String firstName = oAuthUser.getAttribute("given_name");
        String lastName = oAuthUser.getAttribute("family_name");
        String providerId = oAuthUser.getAttribute("sub");
        User user = userRepository.findByEmail(email)
                .orElseGet(()->{
                    User u =  new User();
                    u.setEmail(email);
                    u.setNom(lastName);
                    u.setPrenom(firstName);
                    u.setTelephone(null);
                    u.setPassword("azerty");
                    RolesEntity rolesEntity = rolesEntityRepository.findByName(Roles.CLIENT)
                                    .orElseThrow(()-> new ResourceNotFoundException("role Client not fond"));
                    u.setRole(rolesEntity);
                    return  u;
                });
        user.setProvider(AuthProvider.GOOGLE);
        user.setProviderId(providerId);
        userRepository.save(user);
        var jwt = jwtService.generateToken(user);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().println(jwt);
    }
}
