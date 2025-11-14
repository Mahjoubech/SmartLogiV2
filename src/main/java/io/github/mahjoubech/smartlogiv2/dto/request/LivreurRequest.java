package io.github.mahjoubech.smartlogiv2.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LivreurRequest {
    @NotBlank(message = "le nom est obligatoire")
    private String nom;
    @NotBlank(message = "le prenom est obligatoire")
    private String prenom;
    @NotBlank(message = "le telephone est obligatoire")
    private String telephone;
    @NotBlank(message = "le vehucule est obligatoire")
    private String vehicule;
    @NotBlank(message = "La zone d'affectation est obligatoire.")
    private String zoneAssigneeId;
}
