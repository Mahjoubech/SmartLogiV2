package io.github.mahjoubech.smartlogiv2.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.Set;

@Entity
@Data
@Table(name = "destinataire")
@NoArgsConstructor
@AllArgsConstructor
public class Destinataire extends BaseEntity {
    @Column(name = "nom", nullable = false)
    private String nom;
    @Column(name = "prenom", nullable = false)
    private String prenom;
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    @Column(name = "telephone", nullable = false, unique = true)
    private String telephone;
    @Column(name = "adresse", nullable = false)
    private  String adresse;
     @OneToMany(mappedBy = "destinataire")
     private Set<Colis> colis;

}
