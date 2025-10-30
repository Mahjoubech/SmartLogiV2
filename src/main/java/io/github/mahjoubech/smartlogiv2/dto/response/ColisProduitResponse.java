package io.github.mahjoubech.smartlogiv2.dto.response;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class ColisProduitResponse {
    private String colisId;
    private ProduitResponse produit;
    private Integer quantite;
    private BigDecimal prixUnitaire;
}
