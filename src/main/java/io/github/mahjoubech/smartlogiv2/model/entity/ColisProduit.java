package io.github.mahjoubech.smartlogiv2.model.entity;

import io.github.mahjoubech.smartlogiv2.utils.ColisProduitId;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public class ColisProduit {
    private ColisProduitId colisProduitId;
    private Colis colis;
    private Produit produit;
    private Integer quantite;
    private BigDecimal prixUnitaire;
    private ZonedDateTime dateAjout;
}
