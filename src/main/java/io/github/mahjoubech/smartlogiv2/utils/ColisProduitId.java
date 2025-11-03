package io.github.mahjoubech.smartlogiv2.utils;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Setter
@Getter
public class ColisProduitId implements Serializable {
    @Column(name = "colis_id")
    private String colisId;
    @Column(name = "produit_id")
    private String produitId;
}
