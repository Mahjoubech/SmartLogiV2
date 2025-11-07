package io.github.mahjoubech.smartlogiv2.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
@Data
public class ProduitRequest {
    @NotBlank(message = "Le nom du produit est obligatoire")
    private String nom;
    @NotBlank(message = "La catégorie du produit est obligatoire")
    private String categorie;
    @NotNull(message = "Le poids du produit est obligatoire")
    @DecimalMin(value = "0.5", message = "Le poids doit être supérieur à 0.5 kg.")
    @DecimalMax(value = "70.5", message = "Le poids doit être inférieur à 70.5 kg.")

    private Double poids;
    @NotNull(message = "Le prix du produit est obligatoire")
    @Min(value = 0)
    private BigDecimal prix;
    private ColisProduitRequest ColisProduit;
}
