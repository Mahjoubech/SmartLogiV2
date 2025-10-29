package io.github.mahjoubech.smartlogiv2.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LivreurRequest {
    @NotBlank
    private String nom;
    private String prenom;
    private String telephone;
    private String vehicule;

    @NotBlank(message = "La zone d'affectation est obligatoire.")
    private String zoneAssigneeId;
}
