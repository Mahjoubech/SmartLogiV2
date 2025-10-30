package io.github.mahjoubech.smartlogiv2.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ColisRequest {
    @NotNull(message = "Le poids est obligatoire.")
    @Min(value = 0, message = "Le poids doit être positif.")
    private Double poids;

    private String description;
    private String priorite; // HAUTE, NORMALE, FAIBLE (String li ghadi ytsawa b'Enum)

    @NotBlank(message = "La ville de destination est obligatoire.")
    private String villeDestination;

    // Foreign Keys li khass l'Client y3tihom
    @NotBlank(message = "L'expéditeur est obligatoire.")
    private String clientExpediteurId;

    @NotBlank(message = "Le destinataire est obligatoire.")
    private String destinataireId;

    @NotBlank(message = "La zone de destination est obligatoire.")
    private String zoneId;

    private List<ColisProduitRequest> produits;
}
