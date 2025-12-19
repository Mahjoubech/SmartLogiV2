package io.github.mahjoubech.smartlogiv2.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AuthResponse {
    private String nomComplet;
    private String email;
    private String role;
    private String token;

}
