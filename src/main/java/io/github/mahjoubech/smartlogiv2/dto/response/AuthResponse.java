package io.github.mahjoubech.smartlogiv2.dto.response;

import io.github.mahjoubech.smartlogiv2.dto.response.basic.RolesResponesBasic;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AuthResponse {
    private String nom;
    private String prenom;
    private String email;
    private RolesResponesBasic role;
    private String token;

}
