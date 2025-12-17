package io.github.mahjoubech.smartlogiv2.dto.response.detail;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class ColisProduitResponse {
    private String id;
    private ProduitResponse produit;
    private Integer quantite;
    private BigDecimal prixUnitaire;
}
