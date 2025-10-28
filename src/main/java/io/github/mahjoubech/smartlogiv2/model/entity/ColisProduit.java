package io.github.mahjoubech.smartlogiv2.model.entity;

import io.github.mahjoubech.smartlogiv2.utils.ColisProduitId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Entity
@Data
@Table(name = "colis_produit")
@NoArgsConstructor
@AllArgsConstructor
public class ColisProduit {
    @EmbeddedId
    private ColisProduitId colisProduitId;

    private Colis colis;
    private Produit produit;
    @Column(name = "quantite", nullable = false)
    private Integer quantite;
    @Column(name = "prix_unitaire", nullable = false)
    private BigDecimal prixUnitaire;
    @Column(name = "date_ajout", nullable = false)
    private ZonedDateTime dateAjout;
}
