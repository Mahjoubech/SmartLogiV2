package io.github.mahjoubech.smartlogiv2.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ClientDestinataireRequest {
    @NotBlank(message = "Le nom est obligatoire.")
    private String nom;
    private String prenom;

    @Email(message = "L'email doit être valide.")
    @NotBlank(message = "L'email est obligatoire.")
    private String email;

    private String telephone;
    private String adresse;
}
