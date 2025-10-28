package io.github.mahjoubech.smartlogiv2.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
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
}
