package io.github.mahjoubech.smartlogiv2.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GestionnerRequest {
    @NotNull(message = "Le nom est obligatoire.")
    private String nom;
    @NotNull(message = "Le Prenom est obligatoire.")
    private String prenom;
    @Email(message = "L'email doit être valide.")
    private String email;
    @NotNull(message = "Telephone est obligatoire")
    private String telephone;
    @NotNull(message = "password")
    private String password;
    @NotNull(message = "La confirmation du mot de passe est obligatoire.")
    private String confirmPassword;
}
