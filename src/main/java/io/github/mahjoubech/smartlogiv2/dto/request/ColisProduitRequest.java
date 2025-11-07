package io.github.mahjoubech.smartlogiv2.dto.request;

import jakarta.validation.constraints.Max;
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
    @NotNull(message = "Le produit ne peut pas être nul.")
    @Min(value = 1 , message = "La quantité doit être comprise entre 1 et 20.")
    @Max(value = 20 ,message = "La quantité doit être comprise entre 1 et 20.")
    private Integer quantite;

}
