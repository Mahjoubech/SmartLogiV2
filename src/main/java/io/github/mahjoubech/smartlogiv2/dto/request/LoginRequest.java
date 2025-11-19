package io.github.mahjoubech.smartlogiv2.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class LoginRequest {
        @Email
        @NotBlank(message = "L'email est obligatoire.")
        private String email;
        @NotBlank(message = "Le mot de passe est obligatoire.")
        private String password;
    }


