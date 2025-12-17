package io.github.mahjoubech.smartlogiv2.model.entity;

import io.github.mahjoubech.smartlogiv2.model.enums.ColisStatus;
import io.github.mahjoubech.smartlogiv2.model.enums.PrioriteStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.GenericGenerator;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@SuperBuilder
@Table(name = "colis")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
public class Colis extends BaseEntity{

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "poids", nullable = false)
    private double poids;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut")
    private ColisStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "priorite", nullable = false)
    private PrioriteStatus prioriteStatus;

    @Column(name = "ville_destination", nullable = false)
    private  String villeDestination;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_expediteur_id", nullable = false)
    private ClientExpediteur clientExpediteur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destinataire_id", nullable = false)
    private Destinataire destinataire;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "livreur_id")
    private Livreur livreur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id", nullable = false)
    private Zone zone;

    @OneToMany(mappedBy = "colis", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HistoriqueLivraison> historique;

    @OneToMany(mappedBy = "colis", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ColisProduit> produits;

}