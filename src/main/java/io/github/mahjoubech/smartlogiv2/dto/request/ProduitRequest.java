package io.github.mahjoubech.smartlogiv2.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
@Data
public class ProduitRequest {
    @NotBlank(message = "Le nom du produit est obligatoire")
    private String nom;
    @NotBlank(message = "La catégorie du produit est obligatoire")
    private String categorie;
    @NotNull(message = "Le poids du produit est obligatoire")
    @Size(min = 1, max = 70)
    private Double poids;
    @NotNull(message = "Le prix du produit est obligatoire")
    @Min(value = 0)
    private BigDecimal prix;
    @NotNull(message = "Les informations du colis sont obligatoires")
    private ColisProduitRequest ColisProduit;
}
