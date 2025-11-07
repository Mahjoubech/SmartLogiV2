package io.github.mahjoubech.smartlogiv2.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Set;

@Entity
@Setter
@Getter
@Table(name = "produit")
@NoArgsConstructor
@AllArgsConstructor
public class Produit {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(name = "nom", nullable = false)
    private String nom;
    @Column(name = "categorie", nullable = false)
    private String categorie;
    @Column(name = "poids")
    private Double poids;
    @Column(name = "prix", nullable = false)
    private BigDecimal prix;
    @OneToMany(mappedBy = "produit")
    private Set<ColisProduit> colis;
    @Column(name = "date_creation", nullable = false, updatable = false)
    private ZonedDateTime dateCreation;
    @PrePersist
    public void prePersist() {
        if (dateCreation == null) {
            dateCreation = ZonedDateTime.now();
        }
    }
}
