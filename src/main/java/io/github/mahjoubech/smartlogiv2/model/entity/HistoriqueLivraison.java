package io.github.mahjoubech.smartlogiv2.model.entity;

import io.github.mahjoubech.smartlogiv2.model.enums.ColisStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Entity
@Data
@Table(name = "historique_livraison")
@NoArgsConstructor
@AllArgsConstructor
public class HistoriqueLivraison {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private  String id;
    private  Colis colis;
    @Enumerated(EnumType.STRING)
    private ColisStatus status;
    @Column(name = "commentaire")
    private String commentaire;
    @Column(name = "date_changement", nullable = false)
    private ZonedDateTime dateChangement;
}
