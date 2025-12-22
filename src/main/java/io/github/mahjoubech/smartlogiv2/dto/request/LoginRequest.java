package io.github.mahjoubech.smartlogiv2.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoginRequest {
        @Email
        @NotNull(message = "L'email est obligatoire.")
        private String email;
        @NotNull(message = "Le mot de passe est obligatoire.")
        private String password;
    }


