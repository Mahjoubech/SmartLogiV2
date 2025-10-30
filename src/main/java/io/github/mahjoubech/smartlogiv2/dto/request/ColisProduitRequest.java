package io.github.mahjoubech.smartlogiv2.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ColisProduitRequest {
    @NotBlank(message = "L'ID du produit est obligatoire.")
    private String produitId;

    @NotNull
    @Min(value = 1)
    private Integer quantite;

    private BigDecimal prixUnitaire;
}
