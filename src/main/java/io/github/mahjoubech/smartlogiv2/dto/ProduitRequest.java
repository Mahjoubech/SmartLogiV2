package io.github.mahjoubech.smartlogiv2.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
@Data
public class ProduitRequest {
    @NotBlank
    private String nom;
    private String categorie;
    private Double poids;
    @NotNull
    @Min(value = 0)
    private BigDecimal prix;
}
