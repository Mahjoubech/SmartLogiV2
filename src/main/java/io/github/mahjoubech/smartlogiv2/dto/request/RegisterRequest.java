package io.github.mahjoubech.smartlogiv2.dto.request;

import io.github.mahjoubech.smartlogiv2.model.enums.Roles;
import io.github.mahjoubech.smartlogiv2.validation.annotation.ValidRegisterRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@ValidRegisterRequest
public class RegisterRequest {
    @NotBlank(message = "Le nom est obligatoire.")
    private String nom;
    @NotBlank(message = "Le Prenom est obligatoire.")
    private String prenom;
    @Email(message = "L'email doit être valide.")
    private String email;
    @NotBlank(message = "Telephone est obligatoire")
    private String telephone;
    @NotBlank(message = "password")
    private String password;
    @NotBlank(message = "La confirmation du mot de passe est obligatoire.")
    private String confirmPassword;
    @NotBlank(message = "Le rôle est obligatoire.")
    private Roles role;

    private String adresse;
    private String vehicule;
    private String zoneAssigned;

}
