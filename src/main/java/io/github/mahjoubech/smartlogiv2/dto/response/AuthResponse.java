package io.github.mahjoubech.smartlogiv2.dto.response;

import io.github.mahjoubech.smartlogiv2.dto.response.basic.RolesResponesBasic;
import io.github.mahjoubech.smartlogiv2.model.entity.RolesEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AuthResponse {
    private String nom;
    private String prenom;
    private String email;
    private RolesEntity role;
    private String token;

}
