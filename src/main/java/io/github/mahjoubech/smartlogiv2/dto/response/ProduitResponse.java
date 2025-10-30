package io.github.mahjoubech.smartlogiv2.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProduitResponse {
    private String id;
    private String nom;
    private String categorie;
    private Double poids;
    private BigDecimal prix;
}
