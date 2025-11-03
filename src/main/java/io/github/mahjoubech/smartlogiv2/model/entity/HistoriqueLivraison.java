package io.github.mahjoubech.smartlogiv2.model.entity;

import io.github.mahjoubech.smartlogiv2.model.enums.ColisStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.Date;

@Entity
@Setter
@Getter
@Table(name = "historique_livraison")
@NoArgsConstructor
@AllArgsConstructor
public class HistoriqueLivraison {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private  String id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "colis_id", nullable = false)
    private  Colis colis;
    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    private ColisStatus status;
    @Column(name = "commentaire")
    private String commentaire;
    @Column(name = "date_changement", nullable = false)
    private ZonedDateTime dateChangement;
    @Column(name = "date_creation", nullable = false, updatable = false)
    private Date createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
    }


}
