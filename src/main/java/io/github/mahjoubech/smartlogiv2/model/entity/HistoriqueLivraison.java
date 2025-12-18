package io.github.mahjoubech.smartlogiv2.model.entity;

import io.github.mahjoubech.smartlogiv2.model.enums.ColisStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;

@Entity
@Data
@SuperBuilder
@Table(name = "historique_livraison")
@NoArgsConstructor
@AllArgsConstructor
public class HistoriqueLivraison extends BaseEntity{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "colis_id", nullable = false)
    private  Colis colis;
    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    private ColisStatus status;
    @Column(name = "commentaire")
    private String commentaire;
    @Column(name = "date_changement", nullable = false)
    private LocalDateTime dateChangement;


}
