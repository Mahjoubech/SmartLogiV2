package io.github.mahjoubech.smartlogiv2.model.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Set;
@Entity
@Data
@SuperBuilder
@Table(name = "livreur")
@AllArgsConstructor
@NoArgsConstructor
public class Livreur extends User {

    @Column(name = "vehicule")
    private String vehicule;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_assignee_id")
    private Zone zoneAssigned;
    @OneToMany(mappedBy = "livreur", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Colis> colisList;
}
