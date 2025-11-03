package io.github.mahjoubech.smartlogiv2.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Setter
@Getter
@Table(name = "zone")
@NoArgsConstructor
@AllArgsConstructor
public class Zone {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(name = "nom", nullable = false)
    private String nom;
    @Column(name = "code_postal", nullable = false)
    private  String codePostal;
    @OneToMany(mappedBy = "zone")
    private Set<Colis> colis;
}
