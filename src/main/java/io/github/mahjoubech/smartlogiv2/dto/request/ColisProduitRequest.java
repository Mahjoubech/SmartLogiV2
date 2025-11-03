package io.github.mahjoubech.smartlogiv2.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ColisProduitRequest {
    @NotBlank(message = "L'ID du produit est obligatoire.")
    private String produitId;

    @NotNull
    @Min(value = 1)
    private Integer quantite;

}
