package io.github.mahjoubech.smartlogiv2.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LivreurRequest {
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
    @NotBlank(message = "le vehucule est obligatoire")
    private String vehicule;
    @NotBlank(message = "La zone d'affectation est obligatoire.")
    private String zoneAssigneeId;
}
