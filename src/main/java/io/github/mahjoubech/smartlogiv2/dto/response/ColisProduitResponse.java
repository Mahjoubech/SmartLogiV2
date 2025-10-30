package io.github.mahjoubech.smartlogiv2.dto.response;

import io.github.mahjoubech.smartlogiv2.utils.ColisProduitId;
import lombok.Data;

import java.math.BigDecimal;
@Data
public class ColisProduitResponse {
    private ColisProduitId colisId;
    private ProduitResponse produit;
    private Integer quantite;
    private BigDecimal prixUnitaire;
}
