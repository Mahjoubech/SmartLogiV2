package io.github.mahjoubech.smartlogiv2.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors ->{})
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/api/v2/auth/**").permitAll();
                    auth.requestMatchers("/api/v3/clients/**").hasAnyRole("MANAGER" , "CLIENT");
                    auth.requestMatchers("/api/v1/gestionner/livreur/**").hasAnyRole("MANAGER" , "LIVREUR");
                    auth.requestMatchers("/api/v1/colis").hasAnyRole("CLIENT");
                    auth.requestMatchers("/api/v2/colis/**").hasAnyRole("MANAGER" ,"CLIENT" , "LIVREUR");
                    auth.requestMatchers("/api/v4/gestion/**").hasRole("MANAGER");
                    auth.anyRequest().authenticated();
                })

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(ex -> ex

                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            response.setContentType("application/json");

                            String json = """
                    {
                      "status": 401,
                      "message": "Unauthorized: Authentication is required"
                    }
                    """;

                            response.getWriter().write(json);
                        })

                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpStatus.FORBIDDEN.value());
                            response.setContentType("application/json");

                            String json = """
                    {
                      "status": 403,
                      "message": "Forbidden: You don't have permission to access this resource"
                    }
                    """;

                            response.getWriter().write(json);
                        })
                )
                .authenticationProvider(authenticationProvider)

                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
