package io.github.mahjoubech.smartlogiv2.dto.request;

import io.github.mahjoubech.smartlogiv2.model.entity.Produit;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class ColisRequest {
    @NotNull(message = "Le poids est obligatoire.")
    @Min(value = 0, message = "Le poids doit être positif.")
    private Double poids;
    @NotBlank(message = "L'adresse est obligatoire.")
    private String description;
    @NotBlank(message = "La priorité est obligatoire.")
    @Pattern(regexp = "URGENT|NORMAL|BASIQUE" , message = "La priorité doit être URGENT, NORMAL ou BASIQUE.")
    private String priorite;

    @NotBlank(message = "La ville de destination est obligatoire.")
    private String villeDestination;
    @Email
    @NotBlank(message = "L'expéditeur est obligatoire.")
    private String clientExpediteurEmail;
    @Email
    @NotBlank(message = "Le destinataire est obligatoire.")
    private String destinataireEmail;
    @NotBlank(message = "Le code postal est obligatoire.")
    private String codePostal;
    @NotNull(message = "La liste des produits est obligatoire.")
    private List<ProduitRequest> produits;
}
