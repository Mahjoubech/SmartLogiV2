package io.github.mahjoubech.smartlogiv2.model.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Entity
@Data
@SuperBuilder
@Table(name = "colis_produit")
@NoArgsConstructor
@AllArgsConstructor
public class ColisProduit extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "colis_id", nullable = false)
    private Colis colis;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produit_id", nullable = false)
    private Produit produit;
    @Column(name = "quantite", nullable = false)
    private Integer quantite;
    @Column(name = "prix_unitaire", nullable = false)
    private BigDecimal prixUnitaire;
    @Column(name = "date_ajout", nullable = false)
    private LocalDateTime dateAjout;

}
