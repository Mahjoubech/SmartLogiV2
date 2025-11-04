package io.github.mahjoubech.smartlogiv2.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ClientDestinataireRequest {
    @NotBlank(message = "Le nom est obligatoire.")
    private String nom;
    @NotBlank(message = "Le Prenom est obligatoire.")
    private String prenom;

    @Email(message = "L'email doit être valide.")
    @NotBlank(message = "L'email est obligatoire.")
    private String email;
    @NotBlank(message = "Telephone est obligatoire")
    private String telephone;
    @NotBlank(message = "Adresse est obligatoire")

    private String adresse;
}
