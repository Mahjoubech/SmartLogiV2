package io.github.mahjoubech.smartlogiv2.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Setter
@Getter
@Table(name = "livreur")
@NoArgsConstructor
@AllArgsConstructor
public class Livreur {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(name = "nom", nullable = false)
    private String nom;
    @Column(name = "prenom", nullable = false)
    private String prenom;
    @Column(name = "telephone", nullable = false, unique = true)
    private String telephone;
    @Column(name = "vehicule")
    private String vehicule;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_assignee_id")
    private Zone zoneAssigned;
    @OneToMany(mappedBy = "livreur", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Colis> colisList;
}
