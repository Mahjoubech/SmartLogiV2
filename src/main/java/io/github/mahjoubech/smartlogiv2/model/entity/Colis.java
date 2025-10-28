package io.github.mahjoubech.smartlogiv2.model.entity;

import io.github.mahjoubech.smartlogiv2.model.enums.ColisStatus;
import io.github.mahjoubech.smartlogiv2.model.enums.PrioriteStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@Table(name = "colis")
@NoArgsConstructor
@AllArgsConstructor
public class Colis {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(name = "description", nullable = false)
    private String description;
    @Column(name = "weight", nullable = false)
    private double weight;
    @Enumerated(EnumType.STRING)
    private ColisStatus status;
    @Enumerated(EnumType.STRING)
    private PrioriteStatus prioriteStatus;
    @Column(name = "ville_destination", nullable = false)
    private  String villeDestination;
    @Column(name = "date_creation", nullable = false)
    private Date dateCreation;
    private ClientExpediteur clientExpediteur;
    private Destinataire destinataire;
    private Livreur livreur;
    private Zone zone;
}
