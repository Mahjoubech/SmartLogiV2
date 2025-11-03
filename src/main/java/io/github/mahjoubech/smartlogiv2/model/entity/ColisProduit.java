package io.github.mahjoubech.smartlogiv2.model.entity;

import io.github.mahjoubech.smartlogiv2.utils.ColisProduitId;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Date;

@Entity
@Setter
@Getter
@Table(name = "colis_produit")
@NoArgsConstructor
@AllArgsConstructor
public class ColisProduit {
    @EmbeddedId
    private ColisProduitId colisProduitId;
    @MapsId("colisId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "colis_id", nullable = false)
    private Colis colis;
    @MapsId("produitId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produit_id", nullable = false)
    private Produit produit;
    @Column(name = "quantite", nullable = false)
    private Integer quantite;
    @Column(name = "prix_unitaire", nullable = false)
    private BigDecimal prixUnitaire;
    @Column(name = "date_ajout", nullable = false)
    private ZonedDateTime dateAjout;
    @Column(name = "date_creation", nullable = false, updatable = false)
    private Date dateCreation;
    @PrePersist
    public void prePersist() {
        if (dateCreation == null) {
            dateCreation = new Date();
        }
    }
}
